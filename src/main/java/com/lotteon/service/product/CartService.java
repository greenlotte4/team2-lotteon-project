package com.lotteon.service.product;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.cartOrder.PostCartDto;
import com.lotteon.dto.responseDto.cartOrder.GetCartDto;
import com.lotteon.dto.responseDto.cartOrder.CartProductDto;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.product.*;
import com.lotteon.repository.impl.CartItemOptionRepositoryImpl;
import com.lotteon.repository.impl.CartItemRepositoryImpl;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.product.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ResponseEntity<?> insertCart(PostCartDto postCartDto, HttpSession session) {

        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        // 인증 정보가 없는 경우
        if (auth == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated. Cart saved to session.");
        }

        if(auth.getUser().getMemRole()=="seller"){
            return ResponseEntity.status(HttpStatus.CONTINUE).body("seller");
        }

        Customer customer = auth.getUser().getCustomer();
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found");
        }

        long custId = customer.getId();

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

            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(postCartDto.getQuantity())
                    .totalPrice(postCartDto.getTotalPrice())
                    .optionId(optionId)
                    .build();


            cartItemRepository.save(newCartItem);

        }

        return ResponseEntity.ok().body("insert");
    }

    public List<GetCartDto> selectCart(HttpSession session) {

        // 1. 인증된 사용자 정보 가져오기
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        // 2. 사용자 ID로 카트 조회
        long custId = auth.getUser().getCustomer().getId();
        Optional<Cart> cartOptional = cartRepository.findByCustId(custId);

        // 3. 카트가 없으면 빈 리스트 반환
        if (!cartOptional.isPresent()) {
            return Collections.emptyList();  // 카트가 없을 경우
        }

        Cart cart = cartOptional.get();

        // 4. 카트에 담긴 아이템 목록을 조회
        List<CartItem> cartItems = cart.getItems();
        List<GetCartDto> cartDtoList = new ArrayList<>();

        // 5. 각 카트 아이템을 DTO로 변환
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();  // 카트 아이템에 연결된 상품
            CartProductDto cartProductDto = modelMapper.map(product, CartProductDto.class);

            //옵션이 있으면 옵션리스트에 담기
            Long optionId = null;
            int additionalPrice = 0;
            List<String> optionValue = new ArrayList<>();
            if(cartItem.getOptionId()!=null){
                optionId = cartItem.getOptionId();
                ProductOption option = productOptionRepository.findById(optionId).orElse(null);

                if (option.getOptionValue() != null) {
                    optionValue.add(option.getOptionValue());
                }
                if (option.getOptionValue2() != null) {
                    optionValue.add(option.getOptionValue2());
                }
                if (option.getOptionValue3() != null) {
                    optionValue.add(option.getOptionValue3());
                }
                if(option.getAdditionalPrice() != null){
                    additionalPrice = option.getAdditionalPrice().intValue();
                }

            }
            log.info(" 옵션 밸류 볼래용 "+optionValue.toString());

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
}

