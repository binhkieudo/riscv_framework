#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <riscv-pk/encoding.h>
#include "include/platform.h"
#include "kprintf.h"
#include "sbrk.h"
#include <assert.h>

void *malloc(size_t size) {
  void *p = _sbrk(0);
  void *request = _sbrk(size);
  if (request == (void*) -1) {
    return NULL; // sbrk failed.
  } else {
    assert(p == request); // Not thread safe.
    return p;
  }
}

int main(void) {
  char *str;

  kputs("INIT");
  /* Initial memory allocation */
  str = (char *) malloc(15);
  strcpy(str, "tutorialspoint");
  
  REG32(uart, UART_REG_TXCTRL) = UART_TXEN;
  kprintf("String= %s\r\n", str);

  return 0;
}
