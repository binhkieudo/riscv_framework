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

int mat[400];

void foo(int hartid, unsigned int thread_id) { 
	// mux_lock();
	// kprintf("Core %x run thread 0x%x\r\n", hartid, thread_id);
	// for (int i = 0; i < 100000; i++);
	// mux_unlock();
	mat[thread_id*100] = hartid;
	for (int i = 1; i < 100; i = i + 1){
		mat[thread_id*100 + i] = mat[thread_id*100 + i - 1] + 1;
	}
} 

int main(int thread_0, char** dump)
{

	REG32(uart, UART_REG_TXCTRL) = UART_TXEN;

	thread_init();
	thread_create(&foo);
	thread_create(&foo);
	thread_create(&foo);
	thread_create(&foo);
	// for (int i = 0; i < 100; i = i + 1)
	// 	thread_create(&foo);
	thread_join();

	kputs("\r\n");
	kputs("START\r\n");
	for (int i = 0; i < 400; i++) kprintf("A= %d\r\n", mat[i]);
	kputs("END\r\n");
	while (1);

	return 0;
}


