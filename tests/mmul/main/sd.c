// See LICENSE.Sifive for license details.
#include <stdint.h>
#include <stdio.h>
#include "include/devices/clint.h"
#include <platform.h>

#include "common.h"
#include "smp.h"
#include "kprintf.h"
#include "asm.h"
#include "thread.h"

#define DEBUG

void foo(int hartid, unsigned int thread_id) { 
	mux_lock();
	for (int i = 0; i < 100000; i++);
	kprintf("Core %x run thread 0x%x\r\n", hartid, thread_id);
	for (int i = 0; i < 10000000; i++);
	mux_unlock();
} 

void foo2(int hartid, unsigned int thread_id) { 
	mux_lock();
	kprintf("Hart %x run thread 0x%x\r\n", hartid, thread_id);
	for (int i = 0; i < 10000000; i++);
	mux_unlock();
} 

int main(int thread_0, char** dump)
{

	REG32(uart, UART_REG_TXCTRL) = UART_TXEN;

	thread_init();
	for (int i = 0; i < 100; i = i + 1)
		thread_create(&foo);
	thread_join();

	while (1);

	return 0;
}
