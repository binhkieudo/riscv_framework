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

#define AVAILABLE 	1
#define UNAVAILABLE 0

#define REF_SIZE 65536
#define REF 128
#define WORD 64
#define NCORE 4

struct Data {
	struct Data *next;
	unsigned char point;
	unsigned char state;
};

unsigned char ref[REF_SIZE];
unsigned char se[WORD];
unsigned char map[WORD][REF_SIZE];
char tpoint[REF];

struct Data *c0_ptr;
struct Data *c1_ptr;
struct Data *c2_ptr;
struct Data *c3_ptr;

struct Data local2[WORD][REF_SIZE];

void foo(int hartid, unsigned int thread_id, unsigned int core_id) {
	unsigned char in_word = 'A';  
	unsigned char local_ref[REF];
	struct Data *inDataPtr;
	struct Data *inDataPrevPtr;
	struct Data local[REF];

	int topPoint = 0;
	int diagPoint = 0;
	int leftPoint = 0;
	int maxPoint = 0;

	unsigned int row = 0;

	// Initialize data
	if (hartid == 0) in_word = 'A';
	if (hartid == 1) in_word = 'C';
	if (hartid == 2) in_word = 'G';
	if (hartid == 3) in_word = 'T';

	row = NCORE - 1 - hartid;

	for (int i = 0; i < REF; i = i + 1) {
		if (i == REF - 1) local[i].next = NULL;
		else local[i].next = &local[i+1];
		local[i].state = UNAVAILABLE;
		local[i].point = 0;
		local_ref[i] = ref[thread_id*REF + i];
	}

	// Synchronize
	if (hartid == 3) {
		c3_ptr = local;
		mux_unlock_n(2);
	}
	else if (hartid == 2) {
		mux_lock_n();
		c2_ptr = local;
		inDataPtr = c3_ptr;
		mux_unlock_n(1);
	}
	else if (hartid == 1) {
		mux_lock_n();
		c1_ptr = local;
		inDataPtr = c2_ptr;
		mux_unlock_n(0);
	}
	else if (hartid == 0) {
		mux_lock_n();
		c0_ptr = local;
		inDataPtr = c1_ptr;
	}
	inDataPrevPtr = inDataPtr;

	
	for (int step = 0; step < (WORD/NCORE); step++) {
		// Scoring
		for (int i = 0; i < REF; i = i + 1) {
			if (hartid == 3) {
				maxPoint = 0;
				if (step == 0) {
					topPoint = -2;
					if (in_word == local_ref[i]) diagPoint = 1;
					else diagPoint = -1;
					if (i == 0) leftPoint = -2;
					else leftPoint = local[i-1].point - 2;
					if (diagPoint > 0) maxPoint = diagPoint;
					if (leftPoint > maxPoint) maxPoint = leftPoint;
					local[i].point = maxPoint;
					local[i].state = AVAILABLE;
				}
				else {
					local[i+1].state = UNAVAILABLE;
					topPoint = inDataPtr->point - 2;
					if (in_word == local_ref[i]) {
						if (i == 0) diagPoint = 1;
						else diagPoint = inDataPrevPtr->point + 1;
					}
					else {
						if (i == 0) diagPoint = -1;
						else diagPoint = inDataPrevPtr->point - 1;						
					}
					if (i == 0) leftPoint == -2;
					else leftPoint = local[i-1].point - 2;
					if (topPoint > maxPoint) maxPoint = topPoint;
					if (diagPoint > maxPoint) maxPoint = diagPoint;
					if (leftPoint > maxPoint) maxPoint = leftPoint;
					local[i].point = (unsigned char)maxPoint;

					local[i].state = AVAILABLE;
					inDataPrevPtr = inDataPtr;
					inDataPtr = inDataPtr->next;
				}
			}
			else {
				unsigned char in_state = inDataPtr->state;
				while (in_state != AVAILABLE)
					in_state = inDataPtr->state;
				
				if (step != 0) local[i+1].state = UNAVAILABLE;

				maxPoint = 0;
				topPoint = inDataPtr->point - 2;
				if (in_word == local_ref[i]) diagPoint = inDataPrevPtr->point + 1;
				else diagPoint = inDataPrevPtr->point - 1;
				if (i == 0) leftPoint = -2;
				else leftPoint = local[i-1].point - 2;
				if (topPoint > maxPoint) maxPoint = topPoint;
				if (diagPoint > maxPoint) maxPoint = diagPoint;
				if (leftPoint > maxPoint) maxPoint = leftPoint;
				local[i].point = (unsigned char)maxPoint;

				local[i].state = AVAILABLE;
				inDataPrevPtr = inDataPtr;
				inDataPtr = inDataPtr->next;

			}
		}

		// Rewrite
		for (int i = 0; i < REF; i = i + 1) {
			map[row][thread_id*REF + i] = local[i].point;
		}
		row = row + NCORE;

		// Synchronize
		if (hartid == 0) {
			mux_unlock_n(3);
			mux_lock_n();
			inDataPtr = c1_ptr;
		}
		if (hartid == 3) {
			mux_lock_n();
			inDataPtr = c0_ptr;
			local[0].state = UNAVAILABLE;
			mux_unlock_n(2);
		}
		if (hartid == 2) {
			mux_lock_n();
			inDataPtr = c3_ptr;
			local[0].state = UNAVAILABLE;
			mux_unlock_n(1);
		}
		if (hartid == 1) {
			mux_lock_n();
			inDataPtr = c2_ptr;
			local[0].state = UNAVAILABLE;
			mux_unlock_n(0);
		}			
		inDataPrevPtr = inDataPtr;
	}
} 

void foo2(int hartid, unsigned int thread_id, unsigned int core_id) {
	unsigned char in_word = 'A';  
	
	int topPoint = 0;
	int diagPoint = 0;
	int leftPoint = 0;
	int maxPoint = 0;

	unsigned int row = 0;

	// Initialize data
	if (hartid == 0) in_word = 'A';
	if (hartid == 1) in_word = 'C';
	if (hartid == 2) in_word = 'G';
	if (hartid == 3) in_word = 'T';

	row = NCORE - 1 - hartid;

	// Synchronize
	if (hartid == 3) {
		mux_unlock_n(2);
	}
	else if (hartid == 2) {
		mux_lock_n();
		mux_unlock_n(1);
	}
	else if (hartid == 1) {
		mux_lock_n();
		mux_unlock_n(0);
	}
	else if (hartid == 0) {
		mux_lock_n();
	}

	
	for (int step = 0; step < (WORD/NCORE); step++) {
		// Scoring
		for (int i = 0; i < REF; i = i + 1) {
			if (hartid == 3) {
				maxPoint = 0;
				if (step == 0) {
					topPoint = -2;
					if (in_word == ref[thread_id*REF + i]) diagPoint = 1;
					else diagPoint = -1;
					if (i == 0) leftPoint = -2;
					else leftPoint = local2[row][thread_id*REF + (i-1)].point - 2;
					if (diagPoint > 0) maxPoint = diagPoint;
					if (leftPoint > maxPoint) maxPoint = leftPoint;
					local2[row][thread_id*REF + i].point = maxPoint;
					local2[row][thread_id*REF + i].state = AVAILABLE;
				}
				else {
					local2[row][thread_id*REF + (i+1)].state = UNAVAILABLE;
					topPoint = local2[row-1][thread_id*REF + i].point - 2;
					if (in_word == ref[thread_id*REF + i]) {
						if (i == 0) diagPoint = 1;
						else diagPoint = local2[row-1][thread_id*REF + i - 1].point + 1;
					}
					else {
						if (i == 0) diagPoint = -1;
						else diagPoint = local2[row-1][thread_id*REF + i - 1].point - 1;						
					}
					if (i == 0) leftPoint == -2;
					else leftPoint = local2[row][thread_id*REF + i - 1].point - 2;
					if (topPoint > maxPoint) maxPoint = topPoint;
					if (diagPoint > maxPoint) maxPoint = diagPoint;
					if (leftPoint > maxPoint) maxPoint = leftPoint;
					local2[row][thread_id*REF + i].point = (unsigned char)maxPoint;

					local2[row][thread_id*REF + i].state = AVAILABLE;
				}
			}
			else {
				unsigned char in_state = local2[row-1][thread_id*REF + i].state;
				while (in_state != AVAILABLE)
					in_state = local2[row-1][thread_id*REF + i].state;
				
				if (step != 0) local2[row][thread_id*REF + i + 1].state = UNAVAILABLE;

				maxPoint = 0;
				topPoint = local2[row-1][thread_id*REF + i].point - 2;
				if (in_word == ref[thread_id*REF + i]) diagPoint = local2[row-1][thread_id*REF + i - 1].point + 1;
				else diagPoint = local2[row-1][thread_id*REF + i - 1].point - 1;
				if (i == 0) leftPoint = -2;
				else leftPoint = local2[row][thread_id*REF + i - 1].point - 2;
				if (topPoint > maxPoint) maxPoint = topPoint;
				if (diagPoint > maxPoint) maxPoint = diagPoint;
				if (leftPoint > maxPoint) maxPoint = leftPoint;
				local2[row][thread_id*REF + i].point = (unsigned char)maxPoint;

				local2[row][thread_id*REF + i].state = AVAILABLE;
			}
		}

		// Rewrite
		row = row + NCORE;

		// Synchronize
		if (hartid == 0) {
			mux_unlock_n(3);
			mux_lock_n();
		}
		if (hartid == 3) {
			mux_lock_n();
			mux_unlock_n(2);
		}
		if (hartid == 2) {
			mux_lock_n();
			mux_unlock_n(1);
		}
		if (hartid == 1) {
			mux_lock_n();

			mux_unlock_n(0);
		}			
	}
} 

int main(int thread_0, char** dump)
{

	REG32(uart, UART_REG_TXCTRL) = UART_TXEN;

	unsigned long tstart = 0;
	unsigned long tend = 0;
	
	for (int i = 0; i < WORD; i++) {
		for (int j = 0; j < REF_SIZE; j++)
			map[i][j] = 0;
	}
	
	for (int i = 0; i < REF_SIZE; i++) {
		unsigned int randNum = randi(4);
		if (randNum == 0) ref[i] = 'A';
		else if (randNum == 1) ref[i] = 'C';
		else if (randNum == 1) ref[i] = 'G';
		else ref[i] = 'T';
	}

	for (int i = 0; i < WORD; i++) {
		unsigned int randNum = randi(4);
		if (randNum == 0) se[i] = 'A';
		else if (randNum == 1) se[i] = 'C';
		else if (randNum == 1) se[i] = 'G';
		else se[i] = 'T';
	}

	tstart = read_csr(mcycle);
	int topPoint = 0;
	int diagPoint = 0;
	int leftPoint = 0;
	int maxPoint = 0;
	
	for (int i = 0; i < WORD; i++) {
		for (int j = 0; j < REF_SIZE; j ++) {
			if (i == 0) {
				maxPoint = 0;
				topPoint = -2;
				if (se[i] == ref[j]) diagPoint = 1;
				else diagPoint = -1;
				if (j == 0) leftPoint = -2;
				else leftPoint = map[i][j-1] - 2;

				if (topPoint > maxPoint) maxPoint = topPoint;
				if (diagPoint > maxPoint) maxPoint = diagPoint;
				if (leftPoint > maxPoint) maxPoint = leftPoint;

				map[i][j] = maxPoint;
			}
			else {
				maxPoint = 0;
				topPoint = map[i-1][j] - 2;
				if (se[i] == ref[j]) diagPoint = 1;
				else diagPoint = -1;
				if (j == 0) leftPoint = -2;
				else leftPoint = map[i][j-1] - 2;

				if (topPoint > maxPoint) maxPoint = topPoint;
				if (diagPoint > maxPoint) maxPoint = diagPoint;
				if (leftPoint > maxPoint) maxPoint = leftPoint;

				map[i][j] = maxPoint;
			}
		}
	}
	tend = read_csr(mcycle);
	kprintf("Execution time (single-thread): %ld (cycles)\r\n", tstart);
	kprintf("Execution time (single-thread): %ld (cycles)\r\n", tend);		

	tstart = read_csr(mcycle);
	for (int i = 0; i < (REF_SIZE/REF); i++) {
		thread_init4();
		thread_create(&foo, i, 3);
		thread_create(&foo, i, 2);
		thread_create(&foo, i, 1);
		thread_create(&foo, i, 0);
		thread_join();
	}

	// int topPoint = 0;
	// int diagPoint = 0;
	// int leftPoint = 0;
	// int maxPoint = 0;
	
	// for (int i = 0; i < WORD; i++) {
	// 	for (int j = 0; j < REF_SIZE; j ++) {
	// 		if (i == 0) {
	// 			maxPoint = 0;
	// 			topPoint = -2;
	// 			if (se[i] == ref[j]) diagPoint = 1;
	// 			else diagPoint = -1;
	// 			if (j == 0) leftPoint = -2;
	// 			else leftPoint = map[i][j-1] - 2;

	// 			if (topPoint > maxPoint) maxPoint = topPoint;
	// 			if (diagPoint > maxPoint) maxPoint = diagPoint;
	// 			if (leftPoint > maxPoint) maxPoint = leftPoint;

	// 			map[i][j] = maxPoint;
	// 		}
	// 		else {
	// 			maxPoint = 0;
	// 			topPoint = map[i-1][j] - 2;
	// 			if (se[i] == ref[j]) diagPoint = 1;
	// 			else diagPoint = -1;
	// 			if (j == 0) leftPoint = -2;
	// 			else leftPoint = map[i][j-1] - 2;

	// 			if (topPoint > maxPoint) maxPoint = topPoint;
	// 			if (diagPoint > maxPoint) maxPoint = diagPoint;
	// 			if (leftPoint > maxPoint) maxPoint = leftPoint;

	// 			map[i][j] = maxPoint;
	// 		}
	// 	}
	// }
	tend = read_csr(mcycle);

	kprintf("Execution time (multi-thread): %ld (cycles)\r\n", tstart);
	kprintf("Execution time (multi-thread): %ld (cycles)\r\n", tend);

	while (1);

	return 0;
}


