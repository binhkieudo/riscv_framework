#include <stdio.h>
#include <stdint.h>
#include <riscv-pk/encoding.h>
#include "include/platform.h"
#include "include/devices/clint.h"
#include "kprintf.h"
#include "sections.h"

//#define hart_ctrl  __attribute__((__section__("._harttext")))

static volatile uint32_t * const msip = (void *)(CLINT_CTRL_ADDR);

void print_inorder(void) {
  for (int i = 0; i < 10000; i++);
  REG32(msip, CLINT_MSIP1) = CLINT_MSIPEN;
  for (int i = 0; i < 10000; i++);
  REG32(msip, CLINT_MSIP2) = CLINT_MSIPEN;
  for (int i = 0; i < 10000; i++);
  REG32(msip, CLINT_MSIP3) = CLINT_MSIPEN;
  for (int i = 0; i < 10000; i++);
}

void print_inrevorder(void) {
  for (int i = 0; i < 10000; i++);
  REG32(msip, CLINT_MSIP3) = CLINT_MSIPEN;
  for (int i = 0; i < 10000; i++);
  REG32(msip, CLINT_MSIP2) = CLINT_MSIPEN;
  for (int i = 0; i < 10000; i++);
  REG32(msip, CLINT_MSIP1) = CLINT_MSIPEN;
  for (int i = 0; i < 10000; i++);
}

int main(void) {
  uint64_t m_hartid = read_csr(mhartid);

  REG32(uart, UART_REG_TXCTRL) = UART_TXEN;

  if (m_hartid == 0) kputs("Core 0 from PHAM Lab hello world\n");
  else if (m_hartid == 1) kputs("Core 1 from PHAM Lab hello world\n");
  else if (m_hartid == 2) kputs("Core 2 from PHAM Lab hello world\n");
  else if (m_hartid == 3) kputs("Core 3 from PHAM Lab hello world\n");

  kputs("Print in order\n");
  print_inorder();

  kputs("Print in reverse order\n");
  print_inrevorder();

  return 0;
}
