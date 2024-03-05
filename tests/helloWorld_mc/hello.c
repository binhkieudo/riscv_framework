#include <stdio.h>
#include <stdint.h>
#include <riscv-pk/encoding.h>
#include "include/platform.h"
#include "include/devices/clint.h"
#include "kprintf.h"
#include "sections.h"

//#define hart_ctrl  __attribute__((__section__("._harttext")))

static volatile uint32_t * const msip = (void *)(CLINT_CTRL_ADDR);

int main(void) {
  uint64_t m_hartid = read_csr(mhartid);

  if (m_hartid == 0) {
    REG32(uart, UART_REG_TXCTRL) = UART_TXEN;
    REG32(msip, CLINT_MSIP0) = CLINT_MSIPDIS;
    REG32(msip, CLINT_MSIP1) = CLINT_MSIPDIS;
    REG32(msip, CLINT_MSIP2) = CLINT_MSIPDIS;
    REG32(msip, CLINT_MSIP3) = CLINT_MSIPDIS;
    kputs("Core 0 from PHAM Lab hello world\n");
    REG32(msip, CLINT_MSIP1) = CLINT_MSIPEN;
    for (int i = 0; i < 10000; i++);
    return 0;
  }
  if (m_hartid == 1) {
    REG32(msip, CLINT_MSIP1) = CLINT_MSIPDIS;
    kputs("Core 1 from PHAM Lab hello world\n");
    REG32(msip, CLINT_MSIP2) = CLINT_MSIPEN;
    for (int i = 0; i < 10000; i++);
    return 0;
  }
  if (m_hartid == 2) {
    REG32(msip, CLINT_MSIP2) = CLINT_MSIPDIS;
    kputs("Core 2 from PHAM Lab hello world\n");
    REG32(msip, CLINT_MSIP3) = CLINT_MSIPEN;
    for (int i = 0; i < 10000; i++);
    return 0;
  }
  if (m_hartid == 3) {
    REG32(msip, CLINT_MSIP3) = CLINT_MSIPDIS;
    kputs("Core 3 from PHAM Lab hello world\n");
    REG32(msip, CLINT_MSIP1) = CLINT_MSIPEN;
    for (int i = 0; i < 10000; i++);
    return 0;
  }
  return 0;
}
