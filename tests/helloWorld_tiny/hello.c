// See LICENSE.Sifive for license details.
#include <stdint.h>

#include <platform.h>

#include "common.h"

#define DEBUG
#include "kprintf.h"

int main(void)
{
	REG32(uart, UART_REG_TXCTRL) = UART_TXEN;

	kputs("Hello");

	return 0;
}
