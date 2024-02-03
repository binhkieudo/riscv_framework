// See LICENSE.Sifive for license details.
#include <stdint.h>
#include <stdio.h>
#include "include/devices/clint.h"
#include <riscv-pk/encoding.h>
#include <platform.h>

#include "common.h"
#include "smp.h"
#include "kprintf.h"
#include "asm.h"
#include "thread.h"

#define DEBUG

#define SIZE 64

int A[SIZE][SIZE];
int B[SIZE][SIZE];
int C_single[SIZE][SIZE];
int C_multi[SIZE][SIZE];

void foo(int hartid, unsigned int thread_id) { 
	for (int i = 0; i < SIZE; i++) {
		C_multi[thread_id][i] = 0;
		for (int j = 0; j < SIZE; j++)
			C_multi[thread_id][i] += A[thread_id][j] * B[j][i];
	}
} 

int main(int thread_0, char** dump)
{

	REG32(uart, UART_REG_TXCTRL) = UART_TXEN;

	unsigned long tstart = 0;
	unsigned long tend = 0;
	
	unsigned long tsingle = 0;
	unsigned long tmulti4 = 0;
	unsigned long speed_up = 0;

	unsigned int error = 0;

	for (int i = 0; i < SIZE; i++)
        for (int j = 0; j < SIZE; j++) {
            A[i][j] = randi(100);
			B[i][j] = randi(100);
		}

	// Single-core
	kputs("==> Running with 1 cores...");
	tstart = read_csr(mcycle);
    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            C_single[i][j] = 0;
            for (int k = 0; k < SIZE; k++)
                C_single[i][j] += A[i][k] * B[k][j];
        }
    }
	tend = read_csr(mcycle);
	tsingle = tend-tstart;
	kprintf("Execution time: %ld (cycles)\r\n", tsingle);
	
	// 4-cores
	kputs("==> Running with 4 cores...");
	error = 0;
	tstart = read_csr(mcycle);
	thread_init4();
	for (int i = 0; i < SIZE; i = i + 1) {
		thread_create(&foo, i);
	}
	thread_join();
	tend = read_csr(mcycle);
	tmulti4 = tend-tstart;
	kprintf("Execution time: %ld (cycles)\r\n", tmulti4);

	for (int i = 0; i < SIZE; i = i + 1)
		for (int j = 0; j < SIZE; j = j + 1)
			if (C_single[i][j] != C_multi[i][j]) error++;
	speed_up = tsingle / tmulti4;
	kprintf("Error count: %d \r\n", error);
	kprintf("Speed up (over single): %ld (times)\r\n", speed_up);

	return 0;
}


