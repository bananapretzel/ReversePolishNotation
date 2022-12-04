# Reverse Polish Notation
A second year project where our group was tasked to create a calculator based on reverse polish notation.
## Explanation
*In reverse Polish notation, the operators follow their operands; for instance, to add 3 and 4 together, one would write 3 4 + rather than 3 + 4. If there are multiple operations, operators are given immediately after their final operands (often an operator takes two operands, in which case the operator is written after the second operand); so the expression written 3 − 4 + 5 in conventional notation would be written 3 4 − 5 + in reverse Polish notation: 4 is first subtracted from 3, then 5 is added to it.*

## How to use

Run the program and in the console type operators and operands with a space in between each character.

## Example

| Typical input      | RPN equivalent |
|--------------------|----------------|
| (3 * 4) - ( 5 + 2) | 3 4 * 5 2 + -  |


## Additional Features (because why not)

All binary operators have an equivalent repeating form for ease of use. For example:

| Input      | Output   |
|------------|----------|
| 1 2 3 4 +! | [10]     |
| 1 2 3 4 *! | [24]     |
| 1 2 3 4 -! | [-2]     |

* Special operator "d" duplicates the top item of the current stack.

* Special operator "o" outputs the top item from the current stack.

* Special operator "c" is used to copy the second to last element in the stack *n* times where *n* is the number right before the "c".

* Special operator "r" is used to take the second to last element and insert it into the stack *n* steps behind where *n* is the number right before the "r".

* Parenthesis are used to repeat operations *k* times where *k* is the number ontop of the stack.

Examples:

| Input         | Output       |
|---------------|--------------|
| 1 3 c         | [1, 1, 1]    |
| 2 4 c *!      | [16]         |
| 1 2 3 o       | 3 [1, 2, 3]  |
| 1 2 3 4 2 r   | [1, 2, 4, 3] |
| 1 2 3 4 4 r   | [4, 1, 2, 3] |
| 1 3 ( 2 * )   |  [8]         |
|1 1 1 6 ( + d 3 r )| [21, 13, 21] |
|1 1 10 ( d 3 r + o )| 2 3 5 8 13 21 34 55 89 144 [89, 144] |
|1 3 ( d 2 ( 1 + ) * )| [255] |
