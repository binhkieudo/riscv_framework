#include <stdio.h>
#include <stdint.h>
#include <riscv-pk/encoding.h>
#include "include/platform.h"
#include "kprintf.h"


int main(void) {
  //uint64_t marchid = read_csr(marchid);
  //const char* march = get_march(marchid);
  //printf("Hello world from core 0, a %s\n", march);
  
  
  REG32(uart, UART_REG_TXCTRL) = UART_TXEN;
  kputs("Hello world from PHAM Lab\n");
  return 0;
}
