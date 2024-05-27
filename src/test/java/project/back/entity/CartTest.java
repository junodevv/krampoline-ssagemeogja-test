package project.back.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp(){
        cart = Cart.builder().quantity(2L).build();
    }

    @Test
    @DisplayName("수량 감소 테스트")
    void 수량_감소_테스트(){
        cart.minusQuantity();

        assertThat(cart.getQuantity()).isEqualTo(1L);
    }
    @Test
    @DisplayName("수량 감소 예외 테스트")
    void 수량_감소_예외_테스트_IllegalArgumentException(){
        cart.minusQuantity();
        assertThatThrownBy(() -> cart.minusQuantity()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("수량 증가 테스트")
    void 수량_증가_테스트(){
        cart.plusQuantity();

        assertThat(cart.getQuantity()).isEqualTo(3L);
    }

    @ParameterizedTest
    @CsvSource(value = {"1,1", "5,5", "6,6"})
    @DisplayName("수량 직접입력 테스트")
    void 수량_직접입력_테스트(Long qunatity, Long expected){
        cart.updateQuantity(qunatity);

        assertThat(cart.getQuantity()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    @DisplayName("수량 직접입력 예외 테스트")
    void 수량_직접입력_예외_테스트_IllegalArgumentException(Long quantity){

        assertThatThrownBy(() -> cart.updateQuantity(quantity)).isInstanceOf(IllegalArgumentException.class);
    }
}