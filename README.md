# Computer Architecture and Organization Project

Build a software emulator to emulate the architecture of a computational device

## Getting Started

Required components:

The. [2.0 points] Recorders (5) [A, B, C, D, CI]

B. [2.0 points] CPU

W. [2.0 points] RAM

d. [2.0 points] Bus

and. [2.0 points] Input and Output Module (I / O)


### Prerequisites

The. Word size in bits [16, 32 or 64];

B. RAM size in bytes [8, 16 or 32];

W. Size of the input / output buffer in bytes [4, 8 or 16];

d. Bus width in bits [8, 16 or 32];


### Assembly

The. Instruction mov, move given:

i. "Mov R, i"; where R is any register and i is a 32-bit literal integer. Example: "mov A, 2".

ii. "Mov E, i"; where E is a memory address of the bus width and i is a 32-bit integer. Example: "mov 0x00000001, 2";

iii. "to move";

iv. "Mov R, E";

B. Instruction add, whole addition:

i. "Add X, Y"; where X can be a register or memory address and Y can be a recorder or integer literal. In addition, the result is always stored in operand X. Examples:

1. "add R, i";

2. "add R1, R2";

3. "add 0x00000001, R";

4. "add 0x00000001, 2";

W. Instruction inc, integer increment:

i. "Inc X"; where X may be a register or memory address. Increases the operand by one unit. d. Imul instruction, whole multiplication:

i. "Imul X, Y, Z"; where X can be a register or memory address and Y / Z can be a register, memory address or literal integer. Examples:

1. "imul R1, R2, R3"; R1 = R2 * R3;

2. "imul R1, R2, 5";

3. "imul 0x00000001, 5, R";


### Function

The. The assembly code file is parseado and the instructions are encoded according to the size of the word; each instruction is stored in RAM by the I / O module. The instructions are sent over the bus and stored in RAM cells. The RAM must be represented by an indexed data structure (vector, for example). The bus must also be represented by a data structure and must make the connection between the I / O and the RAM.

B. CPU (can be represented by a function or class) takes the first program instruction in RAM (via bus) and then updates the instruction counter (CI register in this emulator) with the address of this instruction.

W. CPU executes the instruction using registers (variables or objects) and RAM.

d. CPU searches for a new instruction in RAM, updates the CI, and executes the instruction.

and. CPU repeats 2.4. until completion of the program.

f. The emulator should be able to show a "map" of the memory and the result of each instruction being executed in real time.


### Code

The. mov A, 2 (loads the value 2 in register A)

B. mov B, 3 (loads the value 3 in register B)

W. add A, B (add the value in A with the value in B, store the result in A)

d. mov 0x0001, A # loads the value of A at address 0x0001 of RAM

and. inc 0x0001 #increment the value in the operand in a unit

f. imul C, 0x0001, 4 # C = 0x0001 * 4

g. mov 0x0002, C # loads the value of C at address 0x0002 of RAM
