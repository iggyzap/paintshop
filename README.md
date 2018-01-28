# paintshop - naive functional solution

A simple test project to explore maximum of Java 8 functional features

# Usage

First variant:
```bash
$ echo -e "5\n1 G 2 G\n1 M 3 M" | java -jar target/paintshop-root-<version>.jar -stdin [-v]
```

Different variant:
```bash
java -jar target/paintshop-root-<version>.jar <path to file> [-v]
```

# How to build

Run

```bash
mvn clean verify
```
That will compile, test, gather code coverage and shade classes into 1 executable jar file that can be used
to run via _java -jar_ command

#Code coverage

Will be [here](target/site/jacoco/index.html)

#Problem Definition
You want to mix the colors, so that:
- There is just one batch for each color, and it's either gloss or matte.
- For each customer, there is at least one color they like.
- You make as few mattes as possible (because they are more expensive).


# The solution to paintshop problem is following (this one uses depth-first search):
1. Consider that we generate bounded stream of potential solutions, that will record a singular combination of colours,
    per variant of customer's preference. 
1. A resulting solution will have a seen-by of users, correct solution have to satisfy all users.
1. A generator will emit solutions in price increasing order (it will use up all possible combinations of GLOSS paints, and then mattes). 
1. A correct cheapest solution will see all users satisfied in it and being first generated.
1. Solution will not exist when all possible combinations of paints have been exhausted.
