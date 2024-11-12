package com.lotteon.service.point;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostCustCouponDto;
import com.lotteon.dto.responseDto.GetCustomerCouponDto;
import com.lotteon.dto.responseDto.GetMyCouponDto;
import com.lotteon.dto.responseDto.cartOrder.GetCouponDto;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.point.Coupon;
import com.lotteon.entity.point.CustomerCoupon;
import com.lotteon.entity.product.Product;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.member.SellerRepository;
import com.lotteon.repository.point.CouponRepository;
import com.lotteon.repository.point.CustomerCouponRepository;
import com.lotteon.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerCouponService {

    private final CustomerCouponRepository customerCouponRepository;
    private final CouponRepository couponRepository;
    private final CustomerRepository customerRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final RedisTemplate<String,Object> redisTemplate;


    public void postCustCoupon(Long id) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Coupon coupon = couponRepository.findById(id).orElseThrow();
        coupon.updateCouponIssueCnt();

        CustomerCoupon newCustomerCoupon = CustomerCoupon.builder()
                .coupon(coupon)
                .couponExpiration(LocalDate.now().plusDays(coupon.getCustomerCouponExpiration()-1))
                .couponState(1)
                .customer(auth.getUser().getCustomer())
                .build();

        customerCouponRepository.save(newCustomerCoupon);
    }

    public void useCustCoupon(){
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Coupon coupon = couponRepository.findById((long)2).orElseThrow();

        Optional<CustomerCoupon> customerCoupon = customerCouponRepository.findByCustomerAndCoupon(auth.getUser().getCustomer(),coupon);

        if(customerCoupon.isEmpty()){
            return;
        }

        customerCoupon.get().updateCustCouponCntMinus();
    }

    @Scheduled(cron = "0 2 0 * * ?")
    public void deleteCustCoupon(){
        LocalDate today = LocalDate.now();

        List<CustomerCoupon> customerCoupons = customerCouponRepository.findAllByCouponState(0);
        List<CustomerCoupon> expiredCoupons = customerCoupons.stream()
                .filter(coupon -> {
                    LocalDate expirationDate = coupon.getCouponExpiration();
                    return Period.between(expirationDate, today).toTotalMonths() >= 1; // 한 달 이상 차이
                })
                .toList();

        customerCouponRepository.deleteAll(expiredCoupons);


    }

    @Scheduled(cron = "0 1 0 * * ?")
    public void expirateCustCoupon(){
        LocalDate today = LocalDate.now();

        List<CustomerCoupon> customerCoupons = customerCouponRepository.findAll();

        // 만료된 쿠폰 필터링
        List<CustomerCoupon> expiredCoupons = customerCoupons.stream()
                .filter(coupon -> coupon.getCouponExpiration().isBefore(today))
                .toList();

        for(CustomerCoupon customerCoupon : expiredCoupons){
            customerCoupon.updateCustCouponState(0);
        }
    }

    public Page<GetCustomerCouponDto> findAll(int page, String searchType, String keyword) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<GetCustomerCouponDto> dtos ;
        if(!searchType.equals("0")&&!keyword.equals("0")){
            dtos = switch (searchType) {
                case "id" -> this.findAllById(keyword,pageable);
                case "couponId" -> this.findAllByCouponId(keyword,pageable);
                case "couponName" -> this.findAllByCouponName(keyword,pageable);
                default -> this.findAllByCustomer(keyword,pageable);
            };
        } else {
            Page<CustomerCoupon> coupons = customerCouponRepository.findAllByOrderByCouponStateAscIdDesc(pageable);
            dtos = coupons.map(CustomerCoupon::toGetCustomerCouponDto);
        }
        return dtos;
    }

    public Page<GetCustomerCouponDto> findAllBySeller(int page, String searchType, String keyword) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Pageable pageable = PageRequest.of(page, 5);
        Page<GetCustomerCouponDto> dtos ;
        if(!searchType.equals("0")&&!keyword.equals("0")){
            dtos = switch (searchType) {
                case "id" -> this.findAllByIdAndSeller(keyword,pageable,auth.getUser());
                case "couponId" -> this.findAllByCouponIdAndSeller(keyword,pageable,auth.getUser());
                case "couponName" -> this.findAllByCouponNameAndSeller(keyword,pageable,auth.getUser());
                default -> this.findAllByCustomerAndSeller(keyword,pageable,auth.getUser());
            };
        } else {
            List<Coupon> coupons = couponRepository.findAllByMember(auth.getUser());
            List<GetCustomerCouponDto> dtoss = new ArrayList<>();
            for(Coupon coupon : coupons){
                Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponOrderByCouponStateAscIdDesc(coupon,pageable);
                List<GetCustomerCouponDto> dtosFromPage = pageCoupons.map(CustomerCoupon::toGetCustomerCouponDto).getContent();  // 각 페이지의 내용을 리스트로 변환
                dtoss.addAll(dtosFromPage);
            }
            dtos = new PageImpl<>(
                    dtoss,
                    pageable,
                    dtoss.size()
            );
        }
        return dtos;
    }

    private Page<GetCustomerCouponDto> findAllByCustomerAndSeller(String keyword, Pageable pageable, Member user) {
        List<Coupon> coupons = couponRepository.findAllByMemberAndCouponNameContaining(user,keyword);
        List<GetCustomerCouponDto> dtoss = new ArrayList<>();
        Optional<Member> member = memberRepository.findByMemUid(keyword);
        if(member.isEmpty()){
            List<Coupon> coupons2 = couponRepository.findAllByMember(user);
            List<GetCustomerCouponDto> dtoss2 = new ArrayList<>();
            for(Coupon coupon : coupons){
                Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponOrderByCouponStateAscIdDesc(coupon,pageable);
                List<GetCustomerCouponDto> dtosFromPage = pageCoupons.map(CustomerCoupon::toGetCustomerCouponDto).getContent();  // 각 페이지의 내용을 리스트로 변환
                dtoss.addAll(dtosFromPage);
            }
            Page<GetCustomerCouponDto> dtos = new PageImpl<>(
                    dtoss,
                    pageable,
                    dtoss.size()
            );
            return dtos;
        }
        for(Coupon coupon : coupons){
            Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponAndCustomerOrderByCouponStateAscIdDesc(coupon,member.get().getCustomer(),pageable);
            List<GetCustomerCouponDto> dtosFromPage = pageCoupons.map(CustomerCoupon::toGetCustomerCouponDto).getContent();  // 각 페이지의 내용을 리스트로 변환
            dtoss.addAll(dtosFromPage);
        }
        Page<GetCustomerCouponDto> dtos = new PageImpl<>(
                dtoss,
                pageable,
                dtoss.size()
        );
        return dtos;
    }

    private Page<GetCustomerCouponDto> findAllByCouponNameAndSeller(String keyword, Pageable pageable, Member user) {
        List<Coupon> coupons = couponRepository.findAllByMemberAndCouponNameContaining(user,keyword);
        List<GetCustomerCouponDto> dtoss = new ArrayList<>();
        for(Coupon coupon : coupons){
            Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponOrderByCouponStateAscIdDesc(coupon,pageable);
            List<GetCustomerCouponDto> dtosFromPage = pageCoupons.map(CustomerCoupon::toGetCustomerCouponDto).getContent();  // 각 페이지의 내용을 리스트로 변환
            dtoss.addAll(dtosFromPage);
        }
        Page<GetCustomerCouponDto> dtos = new PageImpl<>(
                dtoss,
                pageable,
                dtoss.size()
        );
        return dtos;
    }

    private Page<GetCustomerCouponDto> findAllByCouponIdAndSeller(String keyword, Pageable pageable, Member user) {
        List<Coupon> coupons = couponRepository.findAllByMember(user);
        List<GetCustomerCouponDto> dtoss = new ArrayList<>();
        for(Coupon coupon : coupons){
            Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponAndCouponIdOrderByCouponStateAscIdDesc(coupon,Long.parseLong(keyword),pageable);
            List<GetCustomerCouponDto> dtosFromPage = pageCoupons.map(CustomerCoupon::toGetCustomerCouponDto).getContent();  // 각 페이지의 내용을 리스트로 변환
            dtoss.addAll(dtosFromPage);
        }
        Page<GetCustomerCouponDto> dtos = new PageImpl<>(
                dtoss,
                pageable,
                dtoss.size()
        );
        return dtos;
    }

    private Page<GetCustomerCouponDto> findAllByIdAndSeller(String keyword, Pageable pageable, Member user) {
        List<Coupon> coupons = couponRepository.findAllByMember(user);
        List<GetCustomerCouponDto> dtoss = new ArrayList<>();
        for(Coupon coupon : coupons){
            Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponAndIdOrderByCouponStateAscIdDesc(coupon,Long.parseLong(keyword),pageable);
            List<GetCustomerCouponDto> dtosFromPage = pageCoupons.map(CustomerCoupon::toGetCustomerCouponDto).getContent();  // 각 페이지의 내용을 리스트로 변환
            dtoss.addAll(dtosFromPage);
        }
        Page<GetCustomerCouponDto> dtos = new PageImpl<>(
                dtoss,
                pageable,
                dtoss.size()
        );
        return dtos;
    }

//    private void updateCustCouponState(Coupon coupon) {
//        List<CustomerCoupon> coupons = customerCouponRepository.findAllByCoupon(coupon);
//        coupons.forEach(v->v.updateCustCouponState(1));
//    }
//
//
//    private void deleteExpirationCoupon(Coupon coupon) {
//        List<CustomerCoupon> coupons = customerCouponRepository.findAllByCoupon(coupon);
//        customerCouponRepository.deleteAll(coupons);
//    }

    private Page<GetCustomerCouponDto> findAllByCustomer(String keyword,Pageable pageable) {
        Optional<Member> member = memberRepository.findByMemUid(keyword);
        if(member.isEmpty()){
            Page<CustomerCoupon> coupons = customerCouponRepository.findAllByOrderByCouponStateAscIdDesc(pageable);
            Page<GetCustomerCouponDto> dtos = coupons.map(CustomerCoupon::toGetCustomerCouponDto);
            return dtos;
        }
        Customer customer = member.get().getCustomer();
        Page<CustomerCoupon> coupons = customerCouponRepository.findAllByCustomer(customer,pageable);
        Page<GetCustomerCouponDto> dtos = coupons.map(CustomerCoupon::toGetCustomerCouponDto);

        return dtos;
    }

    private Page<GetCustomerCouponDto> findAllByCouponName(String keyword,Pageable pageable) {
        List<Coupon> coupons = couponRepository.findAllByCouponNameContaining(keyword);
        List<GetCustomerCouponDto> dtoss = new ArrayList<>();  // 최종적으로 모든 DTO를 담을 리스트

        for (Coupon coupon : coupons) {
            Page<CustomerCoupon> pageCoupon = customerCouponRepository.findAllByCouponOrderByCouponStateAscIdDesc(coupon, pageable);
            List<GetCustomerCouponDto> dtosFromPage = pageCoupon.map(CustomerCoupon::toGetCustomerCouponDto).getContent();  // 각 페이지의 내용을 리스트로 변환
            dtoss.addAll(dtosFromPage);
        }
        Page<GetCustomerCouponDto> dtos = new PageImpl<>(
                dtoss,
                pageable,
                dtoss.size()
        );
        return dtos;
    }

    private Page<GetCustomerCouponDto> findAllByCouponId(String keyword,Pageable pageable) {
        Coupon coupon = couponRepository.findById(Long.parseLong(keyword)).orElseThrow();
        Page<CustomerCoupon> coupons = customerCouponRepository.findAllByCouponOrderByCouponStateAscIdDesc(coupon,pageable);
        Page<GetCustomerCouponDto> dtos = coupons.map(CustomerCoupon::toGetCustomerCouponDto);
        return dtos;
    }

    private Page<GetCustomerCouponDto> findAllById(String keyword,Pageable pageable) {
        Page<CustomerCoupon> coupons = customerCouponRepository.findAllByIdOrderByCouponStateAscIdDesc(Long.parseLong(keyword),pageable);
        Page<GetCustomerCouponDto> dtos = coupons.map(CustomerCoupon::toGetCustomerCouponDto);
        return dtos;
    }


    public void updateCouponState(Long id) {
        Optional<CustomerCoupon> customerCoupon = customerCouponRepository.findById(id);
        if(customerCoupon.isEmpty()){
            return;
        }
        if(customerCoupon.get().getCouponState()==1){
            customerCoupon.get().updateCustCouponState(0);
        } else if(customerCoupon.get().getCouponState()==0){
            customerCoupon.get().updateCustCouponState(1);
        }

    }

    public GetCustomerCouponDto findById(Long id) {
        Optional<CustomerCoupon> customerCoupon = customerCouponRepository.findById(id);
        if(customerCoupon.isEmpty()){
            return null;
        }
        GetCustomerCouponDto dto = customerCoupon.get().toGetCustomerCouponDto1();

        return dto;
    }

    public int findAllCntByCustomer() {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        int cnt = customerCouponRepository.countCustomerCouponsByCustomer(auth.getUser().getCustomer());
        return cnt;
    }

    public Page<GetMyCouponDto> findAllByCustomer(int page) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Pageable pageable = PageRequest.of(page,10);


        Page<CustomerCoupon> coupons = customerCouponRepository.findAllByCustomerAndCouponState(auth.getUser().getCustomer(),1,pageable);
        if(coupons.getTotalElements()==0){
            return null;
        }
        Page<GetMyCouponDto> dtos = coupons.map(CustomerCoupon::toGetMyCouponDto);

        return dtos;
    }

    @Cacheable(value = "dailyCoupon", key = "'daily_' + #id + '_' + #auth.user.id + '_' + T(java.time.LocalDate).now()", cacheManager = "cacheManager")
    public String postDailyCoupon(Long id, MyUserDetails auth) {
        Coupon coupon = couponRepository.findById(id).orElseThrow();

        coupon.updateCouponIssueCnt();
        CustomerCoupon newCustomerCoupon = CustomerCoupon.builder()
                .coupon(coupon)
                .couponState(1)
                .couponExpiration(LocalDate.now().plusDays(coupon.getCustomerCouponExpiration()-1))
                .customer(auth.getUser().getCustomer())
                .build();

        customerCouponRepository.save(newCustomerCoupon);
        return "쿠폰 발급이 완료되었습니다.";
    }

    public List<GetCouponDto>   findByCustomerAndSeller(List<Long> prodIds) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Customer customer = auth.getUser().getCustomer();

        List<Member> sellers = this.getSellers(prodIds);
        Set<CustomerCoupon> uniqueCoupons = new HashSet<>();
        System.out.println(sellers);
        for (Member member : sellers) {
            List<CustomerCoupon> couponsFromMember = customerCouponRepository.findAllByCustomerAndCoupon_MemberAndCouponState(customer, member,1);
            uniqueCoupons.addAll(couponsFromMember);
        }

        List<CustomerCoupon> adminCoupons = customerCouponRepository.findAllByCustomerAndCoupon_Member_MemRoleAndCouponState(customer, "admin",1);
        uniqueCoupons.addAll(adminCoupons);
        List<CustomerCoupon> combinedCoupons = new ArrayList<>(uniqueCoupons);

        List<GetCouponDto> dtos = combinedCoupons.stream().map(CustomerCoupon::toCartGetCouponDto).toList();
        return dtos;
    }

    private List<Member> getSellers(List<Long> prodIds) {
        List<Member> members = new ArrayList<>();
        for(Long prodId : prodIds){
            Optional<Product> product = productRepository.findById(prodId);
            Seller sellId = product.get().getSeller();
            Optional<Member> member = memberRepository.findBySeller(sellId);
            members.add(member.get());
        }
        return members;
    }

    public void useCoupon(Long couponId) {
        Optional<CustomerCoupon> coupon = customerCouponRepository.findById(couponId);
        if(coupon.isEmpty()){
            return;
        }
        coupon.get().useCoupon(0);
        coupon.get().getCoupon().updateCouponUseCnt();
    }

}
