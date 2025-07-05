# Stock Formula Query Language (SFQL)

* Implementation of a Shunting Yard parser algorithm for logical expressions.
* Also includes a reverse polish notation evaluator for both logic expressions 
and regular expressions that include variables.
* Variable lookup have to be injected.

### Example

> `(c + 1 > (h1 + 2)) AND ((v > v1 OR avgv10 > 200000) AND c + c > 10)` 

parsed into:

> `[[c, 1, +, h1, 2, +, >], [v, v1, >], [avgv10, 200000, >], [or], [c, c, +, 
>  10, >], [and], [and]]`

and evaluated with `[c = 100, h1 = 5, v = 110000, v1 = 100000 and avgv10 > 
900000]` to:

> `true`
