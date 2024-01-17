#include <stdio.h>
#include <stdint.h>
#include <riscv-pk/encoding.h>
#include "include/platform.h"
#include "kprintf.h"


int main(void) {
  uint64_t m_hartid = read_csr(mhartid);
  
  REG32(uart, UART_REG_TXCTRL) = UART_TXEN;
  

  if (m_hartid == 0) kputs("Core 0 from PHAM Lab hello world\n");
  else if (m_hartid == 1) kputs("Core 1 from PHAM Lab hello world\n");
  else if (m_hartid == 2) kputs("Core 2 from PHAM Lab hello world\n");
  else if (m_hartid == 3) kputs("Core 3 from PHAM Lab hello world\n");

  return 0;
}
