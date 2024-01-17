#include <stdio.h>
#include <stdint.h>
#include <riscv-pk/encoding.h>
#include "include/platform.h"
#include "kprintf.h"


int main(void) {
  uint32_t gpio_val = 0x0000aaaa;
  REG32(gpio, GPIO_OUTPUT_EN) = 0x0000ffff;
  REG32(gpio, GPIO_OUTPUT_VAL) = 0x0000aaaa;
  
  REG32(uart, UART_REG_TXCTRL) = UART_TXEN;
  kputs("Hello world from PHAM Lab\n");

  while (1) {
    for (int i = 0; i < 100000; i = i + 1);
    gpio_val = gpio_val ^ 0x0000ffff;
    REG32(gpio, GPIO_OUTPUT_VAL) = gpio_val;
  }
  return 0;
}
