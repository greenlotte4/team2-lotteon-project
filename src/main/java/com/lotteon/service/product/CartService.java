package com.lotteon.service.product;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.cartOrder.PostCartDto;
import com.lotteon.dto.responseDto.GetOption1Dto;
import com.lotteon.dto.responseDto.cartOrder.GetCartDto;
import com.lotteon.dto.responseDto.cartOrder.CartProductDto;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.product.*;
import com.lotteon.repository.impl.CartItemOptionRepositoryImpl;
import com.lotteon.repository.impl.CartItemRepositoryImpl;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.product.*;
import groovyjarjarantlr4.runtime.misc.IntArray;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ModelMapper modelMapper;

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemOptionRepository cartItemOptionRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final CartItemRepositoryImpl cartItemRepositoryImpl;
    private final CartItemOptionRepositoryImpl cartItemOptionRepositoryImpl;

    public Integer findCartCount(HttpServletRequest req) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String cartId = getCookieValue(req, "cartId");

        log.info("카트 아이디 있나요? "+cartId);
        // 인증이 없는 경우
        if (auth == null || auth.getPrincipal() instanceof String) {
            return handleGuestCartCount(cartId); // 비로그인 사용자의 로직
        }

        // 로그인 여부와 쿠키 내 cartId 여부에 따라 다르게 처리
        Optional<Cart> optCart = Optional.empty();

        if (auth != null) { // 로그인한 상태
            MyUserDetails authDetails = (MyUserDetails) auth.getPrincipal();
            if(authDetails.getUser().getMemRole().equals("admin")) {
                return null;
            } else if (authDetails.getUser().getMemRole().equals("seller")) {
                return null;
            }
            Long custId = authDetails.getUser().getCustomer().getId();
            optCart = cartRepository.findByCustId(custId);

            if (cartId != null) { // 로그인한 상태에서 쿠키에 cartId가 있을 때
                Optional<Cart> cookieCart = cartRepository.findById(Long.parseLong(cartId));
                // 두 카트 모두 존재하면 합쳐서 반환
                return getCombinedCartCount(optCart, cookieCart);
            }

        }

        // 최종적으로 카트 아이템 개수를 반환, null 또는 0인 경우 null 반환
        return optCart
                .map(cart -> cart.getItems().isEmpty() ? null : cart.getItems().size())
                .orElse(null);
    }

    private Integer handleGuestCartCount(String cartId) {
        if (cartId == null) {
            return null;
        }

        Optional<Cart> optCart = cartRepository.findById(Long.parseLong(cartId));
        return optCart.map(cart -> cart.getItems().size()).orElse(null);
    }
    private Integer getCombinedCartCount(Optional<Cart> optCustCart, Optional<Cart> optCookieCart) {
        int count1 = optCustCart.map(cart -> cart.getItems().size()).orElse(0);
        int count2 = optCookieCart.map(cart -> cart.getItems().size()).orElse(0);
        return (count1 + count2) > 0 ? count1 + count2 : null;
    }

    @Transactional
    public ResponseEntity<?> insertCart(PostCartDto postCartDto, Authentication auth) {

        Long custId = null;
        if(auth!=null && auth.getPrincipal() instanceof MyUserDetails){
            MyUserDetails user =(MyUserDetails) auth.getPrincipal();
            custId = user.getUser().getCustomer().getId();
        }
        //장바구니 조회 없으면 생성
        Optional<Cart> optCart = cartRepository.findByCustId(custId);

        Cart cart = null;
        if(!optCart.isPresent()) {
            cart = Cart.builder().custId(custId).build();
            cartRepository.save(cart);
        }else{
            cart = optCart.get();
        }

        return ResponseEntity.ok(cart);

    }
    //비회원 카트
    public String getCookieValue(HttpServletRequest req, String cookieName) {
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // 해당 쿠키가 없을 때 null 반환
    }

    public Cart insertCartFornoAuth(HttpServletRequest req, HttpServletResponse resp) {
        String cartId = getCookieValue(req, "cartId");

        if (cartId == null) {
            Cart cart = cartRepository.save(new Cart());
            Cookie newCookie = new Cookie("cartId", cart.getId().toString());
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24 * 7); // 7일 유효기간
            resp.addCookie(newCookie);
            return cart;
        } else {
            Optional<Cart> optCart = cartRepository.findById(Long.parseLong(cartId));
            if(optCart.isPresent()) {
                Cart cart = optCart.get();
                return cart;
            }else{
                Cookie deleteCookie = new Cookie("cartId", null);
                deleteCookie.setPath("/");
                deleteCookie.setMaxAge(0);
                resp.addCookie(deleteCookie);
                return null;
            }
        }
    }

    public ResponseEntity<?> insertCartItem(PostCartDto postCartDto, Cart cart) {


        Product product = productRepository.findById(postCartDto.getProdId()).orElse(null);

        if(product == null) {
            return ResponseEntity.ok().body("Product not found");
        }

        //카트 아이템 조회하기
        List<CartItem> existingCartItems = cartItemRepository.findAllByCartAndProduct(cart, product);

        //카트 아이템을 돌리면서 같은 상품이 있는지 체크하기
        CartItem existingCartItem = null;
        for(CartItem cartItem : existingCartItems) {
            Long cartItemOptionId = null;
            if(cartItem.getOptionId()!=null) {
                cartItemOptionId = cartItem.getOptionId();
            }
            Long cartItemProdId = cartItem.getProduct().getId();

            if(cartItemOptionId.equals(postCartDto.getOptionId())
            && cartItemProdId.equals(postCartDto.getProdId())) {
                existingCartItem = cartItem;
                break;
            }
        }

        Long optionId = null;
        if(postCartDto.getOptionId()!=null){
             optionId = postCartDto.getOptionId();
        }

        if (existingCartItem != null) {
            int newQuantity = existingCartItem.getQuantity() + postCartDto.getQuantity();
            double newPrice = existingCartItem.getTotalPrice() + postCartDto.getTotalPrice();
            existingCartItem.setTotalPrice(newPrice);
            existingCartItem.setQuantity(newQuantity);
            cartItemRepository.save(existingCartItem);
        } else {
            Optional<ProductOption> option = productOptionRepository.findById(optionId);

            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(postCartDto.getQuantity())
                    .totalPrice(postCartDto.getTotalPrice())
                    .optionId(optionId)
                    .optionCurrAdditional(option.get().getAdditionalPrice())
                    .build();

            cartItemRepository.save(newCartItem);

        }

        return ResponseEntity.ok().body("insert");
    }

    public Cart selectCart(Authentication authentication,
                                     HttpServletRequest req, HttpServletResponse resp) {
        // 1. 인증된 사용자 정보 가져오기
        MyUserDetails auth = (MyUserDetails) authentication.getPrincipal();
        Long custId = auth.getUser().getCustomer().getId();

        String cartId = getCookieValue(req, "cartId");

        Optional<Cart> optCustCart = cartRepository.findByCustId(custId);
        Cart cart;
        if(!optCustCart.isPresent()) {
            log.info("커스터머 카트 없으면 저장해 ");
            cart = cartRepository.save(Cart.builder().custId(custId).build());
            log.info("저장한 카트 "+cart);
        }else {
            if(cartId != null) {
                Optional<Cart> optCookieCart = cartRepository.findById(Long.parseLong(cartId));
                optCookieCart.get().getItems().forEach(cartItem -> {
                    cartItem.setCart(optCustCart.get());
                });
                Cookie newCookie = new Cookie("cartId", null);
                newCookie.setPath("/");
                newCookie.setMaxAge(0);
                resp.addCookie(newCookie);
            }
            cart = optCustCart.get();
        }
        return cart;
    }

    public Cart selectCartFornoAuth(HttpServletRequest req, HttpServletResponse resp) {
        String cartId = getCookieValue(req, "cartId");
        Cart cart;
        if(cartId==null){
            cart = cartRepository.save(new Cart());

            Cookie newCookie = new Cookie("cartId", cart.getId().toString());
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24 * 7);
            resp.addCookie(newCookie);
        }else{
            Optional<Cart> optCart = cartRepository.findById(Long.parseLong(cartId));
            cart = optCart.get();
        }

        return cart;
    }

    public List<GetCartDto> selectCartItem(Cart cart) {
        // 3. 카트가 없으면 빈 리스트 반환
        if (cart.getItems() == null) {
            return Collections.emptyList();  // 카트가 없을 경우
        }

        // 4. 카트에 담긴 아이템 목록을 조회
        List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);
        List<GetCartDto> cartDtoList = new ArrayList<>();

        // 5. 각 카트 아이템을 DTO로 변환
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            CartProductDto cartProductDto = modelMapper.map(product, CartProductDto.class);

            // 옵션이 있으면 옵션 리스트에 담기
            Long optionId = null;
            int additionalPrice = 0;
            List<String> optionValue = new ArrayList<>();
            if (cartItem.getOptionId() != null) {
                optionId = cartItem.getOptionId();
                ProductOption option = productOptionRepository.findById(optionId).orElse(null);

                if (option != null) {
                    if (option.getOptionValue() != null) optionValue.add(option.getOptionValue());
                    if (option.getOptionValue2() != null) optionValue.add(option.getOptionValue2());
                    if (option.getOptionValue3() != null) optionValue.add(option.getOptionValue3());
                    if (option.getAdditionalPrice() != null) additionalPrice = option.getAdditionalPrice().intValue();
                }
            }

            // 6. DTO로 변환
            GetCartDto getCartDto = GetCartDto.builder()
                    .cartItemId(cartItem.getId())
                    .quantity(cartItem.getQuantity())
                    .totalPrice(cartItem.getTotalPrice())
                    .optionId(optionId)
                    .optionValue(optionValue)
                    .cartProductDto(cartProductDto)
                    .additionalPrice(additionalPrice)
                    .build();

            // 7. DTO 리스트에 추가
            cartDtoList.add(getCartDto);
        }

        // 8. DTO 리스트 반환
        return cartDtoList;
    }

    public Long deleteCartItem(List<Long> cartItemIds) {

        Long deletedOption = cartItemOptionRepositoryImpl.deleteCartItemOptionsByCartItemId(cartItemIds);
        log.info("카트아이템 옵션부터 삭제해야 돼 했니? "+deletedOption);

        if(deletedOption!=null){
            Long deletedCount = cartItemRepositoryImpl.deleteCartItemsByCartItemId(cartItemIds);
            return deletedCount;
        }else{
            return null;
        }

    }

    public Map<Long,List<GetOption1Dto>> selectOptions(Cart cart) {

        Map<Long,List<GetOption1Dto>> map = new HashMap<>();
        cart.getItems().forEach(v->{
            List<ProductOption> options = productOptionRepository.findAllByProduct(v.getProduct());
            map.put(v.getProduct().getId(),options.stream().map(ProductOption::toGetCartOptions).toList());
        });
        System.out.println(map);
        return map;
    }

    public void updateCartOption(Long id, Long prod, int quantity) {
        Optional<CartItem> cartItem = cartItemRepository.findById(prod);
        Optional<ProductOption> productOption = productOptionRepository.findById(id);
        if(cartItem.get().getOptionCurrAdditional()!=null){
            cartItem.get().setQuantity(quantity);
            cartItemRepository.save(cartItem.get());
            cartItem.get().updateOption(id,productOption.get().getAdditionalPrice());
            cartItemRepository.save(cartItem.get());
            cartItem.get().updateAdditional(productOption.get().getAdditionalPrice());
            cartItemRepository.save(cartItem.get());
        } else {
            cartItem.get().insertAdditional(id,productOption.get().getAdditionalPrice());
            cartItemRepository.save(cartItem.get());
            cartItem.get().setQuantity(quantity);
            cartItemRepository.save(cartItem.get());
        }
    }

    public void updateQuantity(Long cart, Integer quantity) {
        Optional<CartItem> cartItem = cartItemRepository.findById(cart);
        cartItem.get().setQuantity(quantity);
    }

}

