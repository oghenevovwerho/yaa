# Yaa

Yaa is a programming language for building scalable applications on the JVM ecosystem.
It is a statically typed language with compile time guarantees to ensure that user code does
not suffer from some runtime inefficiencies and errors that plague dynamically type code.

Unlike many new programming languages that seek to attach a functionality to every key
on your keyboard, Yaa follows the philosophy of minimalism-of-constructs. The feature set of 
Yaa though not necessarily minimal, are expressed with just a handful of syntactical constructs. 
This makes the language very approachable to both novices and veteran programmers alike. 
This is due to the fact that the concepts learned in one corner of the language are transferable 
to other parts of the language.

> **MINIMAL** does not mean **VERBOSE**, as the following code snippet demonstrates

``-> out.println(`Hi {getProperty(`user.name`)}, welcome to Yaa!`);``

> The code sample above does a number of things in just one line of code
> 1. It gets the user whose name is used to log into the computer
> 2. It interpolates this name into the surrounding string **Hi \<name\>, welcome to Yaa!**
> 3. It invokes the JVM utility method `print` from `java.lang.System.out`
> 4. The interpolated string is then printed to the terminal

Yaa enables the developers of software to build reliable software as it is built on and
adheres to a core set of philosophies (would definitely grow/(or change) with time :smile:)

1.  **Interoperability:** Yaa code should be able to use any JVM compatible library, also, the
    code produced by the Yaa compiler should be easily usable by other JVM compatible
    languages, tools, frameworks, e.t.c.

2. **Minimalism:** Yaa designed with minimalism in mind. Only the most productive aspects of
   everyday programming is supported. The majority of code constructs are designed to follow
   an easily recognisable pattern. This makes it easy to learn the language and to retain it
   after it has been learned. You do not need to have four different ways to do conditionals.
   A single conditional construct is enough to express decision-making.

3. **DRY:** Do not repeat yourself. Or, in this case, do not repeat the JVM ecosystem. Yaa only
   seeks to provide things that are not already provided by the JVM ecosystem. Why attach a 
   **toDouble** method to an I32 type when the boxed types already have that problem solved?.
   Yaa re-uses the JVM provided utilities as much as possible. It only provides an alternative
   when the available solution is not so convenient to use.

## Running Yaa
The best way to run Yaa for now is by cloning the repo and building the source. This can be done
by following the steps below

1. Make sure that the latest version of the JDK is present in your local machine
2. Clone the Yaa repo into a local directory
3. Use apache Maven to build the source into a Jar or any other executable formats

## Creating a new Yaa project
The best way to create a new Yaa program is by using the yutils program. This is a cli program to
come with a range of functionalities. Other than creating Yaa projects, it can also do more, like

1. Checking to see if Yaa is actually installed
2. Checking the version of the Yaa compiler
3. Checking if there is a working JDK installed on your system