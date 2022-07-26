package com.manhtran.finaltrainning.services;

import com.manhtran.finaltrainning.dtos.CartDTO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class CartService {
        private static final String SESSION_KEY_GIO_HANG = "gioHang";

        public CartDTO getCart(HttpSession session){
            CartDTO cart = (CartDTO) session.getAttribute(SESSION_KEY_GIO_HANG);
            if(cart == null){
                cart = new CartDTO();
            }
            return cart;
        }

        public void setCart(HttpSession session, CartDTO cart){
            session.setAttribute(SESSION_KEY_GIO_HANG, cart);
        }

        public void removeCart(HttpSession session){
            session.removeAttribute(SESSION_KEY_GIO_HANG);
        }
}
