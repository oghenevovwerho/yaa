grammar yaa;

program: '\n'* EOF | (statement '\n'+)* '\n'* EOF;

statement: 'hello' 'world';

skip: ' ';