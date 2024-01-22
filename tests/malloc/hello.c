#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <riscv-pk/encoding.h>
#include "include/platform.h"
#include "kprintf.h"


int main(void) {
  char *str;

  /* Initial memory allocation */
  str = (char *) malloc(15);
  strcpy(str, "tutorialspoint");
  
  REG32(uart, UART_REG_TXCTRL) = UART_TXEN;
  kprintf("String= %s\r\n", str);

  return 0;
}
