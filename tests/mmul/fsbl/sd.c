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

#define STACK_TOP (BRAM_MEM_ADDR + 0xFF0)

volatile uint32_t * thrPtr = (uint32_t *)(STACK_TOP);


int main(int mhartid, char** dump)
{
	unsigned int *info_ptr;
	unsigned int *address_ptr;
	unsigned int info;
	unsigned int address;

	do {
		// Get thread info
		mux_lock();
		info_ptr = (unsigned int *)(*thrPtr - 4);
		address_ptr = (unsigned int*)(*thrPtr - 8);

		info = *info_ptr;
		address = *address_ptr;

		*info_ptr = (mhartid << 29) | info;
		*address_ptr = 0xffffffff;
		*thrPtr	= address - 8;
		mux_unlock();

		if (address == 0xffffffff) break;

		// Run thread
		void (*func_ptr)(int) = (void*)address;
		(*func_ptr)(info); 	
	}
	while (1);
	
	return 0;
}
