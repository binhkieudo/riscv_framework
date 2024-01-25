// See LICENSE.Sifive for license details.
#include <stdint.h>
#include "include/devices/clint.h"
#include <platform.h>

#include "common.h"
#include "smp.h"
#include "asm.h"

#define DEBUG
#include "kprintf.h"
#include "asm.h"

static volatile uint32_t * msip = (void *)(CLINT_CTRL_ADDR);
char * msip0 = (void *)(CLINT_CTRL_ADDR + CLINT_MSIP0);
char * msip1 = (void *)(CLINT_CTRL_ADDR + CLINT_MSIP1);
char * msip2 = (void *)(CLINT_CTRL_ADDR + CLINT_MSIP2);
char * msip3 = (void *)(CLINT_CTRL_ADDR + CLINT_MSIP3);

void delay() {
	for (int i = 0; i < 10000; i ++);
}

int main(int mhartid, char** dump)
{

	REG32(msip, CLINT_MSIP1) = CLINT_MSIPEN;
	REG32(msip, CLINT_MSIP2) = CLINT_MSIPEN;
	REG32(msip, CLINT_MSIP3) = CLINT_MSIPEN;

	if (mhartid == 0) {
		sem_wait(msip0);
		REG32(msip, CLINT_MSIP0) = CLINT_MSIPCLR;
		REG32(uart, UART_REG_TXCTRL) = UART_TXEN;
		kputs("BOOTING from core 0");
		delay();
		REG32(uart, UART_REG_TXCTRL) = UART_TXDIS;
		sem_signal(msip3);
	}
	if (mhartid == 1) {
		sem_wait(msip1);
		REG32(msip, CLINT_MSIP1) = CLINT_MSIPCLR;
		REG32(uart, UART_REG_TXCTRL) = UART_TXEN;
		kputs("BOOTING from core 1");
		delay();
		REG32(uart, UART_REG_TXCTRL) = UART_TXDIS;
	}
    if (mhartid == 2) {

		REG32(msip, CLINT_MSIP2) = CLINT_MSIPCLR;
		REG32(uart, UART_REG_TXCTRL) = UART_TXEN;
		kputs("BOOTING from core 2");
		delay();
		REG32(uart, UART_REG_TXCTRL) = UART_TXDIS;
		sem_signal(msip0);
	}
    if (mhartid == 3) {
		sem_wait(msip3);
		REG32(msip, CLINT_MSIP3) = CLINT_MSIPCLR;
		REG32(uart, UART_REG_TXCTRL) = UART_TXEN;
		kputs("BOOTING from core 3");
		delay();
		REG32(uart, UART_REG_TXCTRL) = UART_TXDIS;
		sem_signal(msip1);
	}

	return 0;
}
