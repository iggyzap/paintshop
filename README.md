# paintshop

A simple test project to explore maximum of Java 8 functional features

# Usage

```bash
$ echo -e "5\n1 G 2 G\n1 M 3 M" | java -jar target/paintshop-root-1.0-SNAPSHOT.jar -stdin
```


# A solution of paintshop problem is following (this one uses exhaustive search):
1. Consider that we generate bounded stream of potential solutions, that will record a singular combination of colours,
    per variant of customer's preference. 
1. A resulting solution will have a seen-by of users, so user's input will be only selected once.
1. We also gather price of solution which will be count of matte paints used.
1. A generator will emit solutions in price increasing order, i.e. starting from 0 mattes max to all mattes in paint buckets. 
1. A correct cheapest solution will see all users satisfied in it and being first generated.
1. Solution will not exist when all max matte paints were tried out.
