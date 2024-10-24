package com.lotteon.service.point;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostCustCouponDto;
import com.lotteon.dto.responseDto.GetCustomerCouponDto;
import com.lotteon.dto.responseDto.GetMyCouponDto;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.entity.point.Coupon;
import com.lotteon.entity.point.CustomerCoupon;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.point.CouponRepository;
import com.lotteon.repository.point.CustomerCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerCouponService {

    private final CustomerCouponRepository customerCouponRepository;
    private final CouponRepository couponRepository;
    private final CustomerRepository customerRepository;
    private final MemberRepository memberRepository;

    public void postCustCoupon(                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            ) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Coupon coupon = couponRepository.findById((long)6).orElseThrow();

        Optional<CustomerCoupon> customerCoupon = customerCouponRepository.findByCustomerAndCoupon(auth.getUser().getCustomer(),coupon);
        if(customerCoupon.isPresent()){
            this.updateCustCouponCnt(customerCoupon);
            return;
        }


        CustomerCoupon newCustomerCoupon = CustomerCoupon.builder()
                .coupon(coupon)
                .couponCnt(1)
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

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteCustCoupon(){
        LocalDate today = LocalDate.now();

        List<Coupon> coupons = couponRepository.findAll();

        for(Coupon coupon : coupons){
            LocalDate end = LocalDate.parse(coupon.getCouponExpiration().substring(coupon.getCouponExpiration().indexOf("~")+1).trim());
            if(today.isAfter(end.plusMonths(1))){
                this.deleteExpirationCoupon(coupon);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void expirateCustCoupon(){
        LocalDate today = LocalDate.now();

        List<Coupon> coupons = couponRepository.findAll();

        for(Coupon coupon : coupons){
            LocalDate start = LocalDate.parse(coupon.getCouponExpiration().substring(0,coupon.getCouponExpiration().indexOf("~")));
            LocalDate end = LocalDate.parse(coupon.getCouponExpiration().substring(coupon.getCouponExpiration().indexOf("~")+1));
            if(today.isBefore(start) && today.isAfter(end)){
                this.updateCustCouponState(coupon);
            }
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
            Page<CustomerCoupon> coupons = customerCouponRepository.findAllByOrderByCouponStateAscCouponUDateDesc(pageable);
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
                Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponOrderByCouponStateAscCouponUDateDesc(coupon,pageable);
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
                Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponOrderByCouponStateAscCouponUDateDesc(coupon,pageable);
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
            Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponAndCustomerOrderByCouponStateAscCouponUDateDesc(coupon,member.get().getCustomer(),pageable);
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
            Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponOrderByCouponStateAscCouponUDateDesc(coupon,pageable);
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
            Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponAndCouponIdOrderByCouponStateAscCouponUDateDesc(coupon,Long.parseLong(keyword),pageable);
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
            Page<CustomerCoupon> pageCoupons = customerCouponRepository.findAllByCouponAndIdOrderByCouponStateAscCouponUDateDesc(coupon,Long.parseLong(keyword),pageable);
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

    private void updateCustCouponState(Coupon coupon) {
        List<CustomerCoupon> coupons = customerCouponRepository.findAllByCoupon(coupon);
        coupons.forEach(CustomerCoupon::updateCustCouponState);
    }


    private void updateCustCouponCnt(Optional<CustomerCoupon> customerCoupon) {
        CustomerCoupon updateCustomerCoupon = customerCoupon.get();
        updateCustomerCoupon.updateCustCouponCnt();
    }

    private void deleteExpirationCoupon(Coupon coupon) {
        List<CustomerCoupon> coupons = customerCouponRepository.findAllByCoupon(coupon);
        customerCouponRepository.deleteAll(coupons);
    }

    private Page<GetCustomerCouponDto> findAllByCustomer(String keyword,Pageable pageable) {
        Optional<Member> member = memberRepository.findByMemUid(keyword);
        if(member.isEmpty()){
            Page<CustomerCoupon> coupons = customerCouponRepository.findAllByOrderByCouponStateAscCouponUDateDesc(pageable);
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
            Page<CustomerCoupon> pageCoupon = customerCouponRepository.findAllByCouponOrderByCouponStateAscCouponUDateDesc(coupon, pageable);
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
        Page<CustomerCoupon> coupons = customerCouponRepository.findAllByCouponOrderByCouponStateAscCouponUDateDesc(coupon,pageable);
        Page<GetCustomerCouponDto> dtos = coupons.map(CustomerCoupon::toGetCustomerCouponDto);
        return dtos;
    }

    private Page<GetCustomerCouponDto> findAllById(String keyword,Pageable pageable) {
        Page<CustomerCoupon> coupons = customerCouponRepository.findAllByIdOrderByCouponStateAscCouponUDateDesc(Long.parseLong(keyword),pageable);
        Page<GetCustomerCouponDto> dtos = coupons.map(CustomerCoupon::toGetCustomerCouponDto);
        return dtos;
    }


    public void updateCouponState(Long id) {
        Optional<CustomerCoupon> customerCoupon = customerCouponRepository.findById(id);
        if(customerCoupon.isEmpty()){
            return;
        }
        if(customerCoupon.get().getCouponState()==1){
            customerCoupon.get().updateCustCouponState();
        } else if(customerCoupon.get().getCouponState()==0){
            customerCoupon.get().updateCustCouponStateMinus();
        } else {
            customerCoupon.get().updateCustCouponState();
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


        Page<CustomerCoupon> coupons = customerCouponRepository.findAllByCustomer(auth.getUser().getCustomer(),pageable);
        if(coupons.getTotalElements()==0){
            return null;
        }
        Page<GetMyCouponDto> dtos = coupons.map(CustomerCoupon::toGetMyCouponDto);

        return dtos;
    }
}
