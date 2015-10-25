# Cache

## Apresentação

Universidade Federal de Pelotas Bacharelado em Ciência da Computação

Disciplina de Arquitetura e Organizaçao de Computadores II

* Pablo Margreff - 14100513 - pmargreff@inf.ufpel.edu.br
* Kellerson Kurtz - 14100507 - kkurtz@inf.ufpel.edu.br

## Como usar
* Compilação: `javac Processor.java`
* Execução: `java Processor <nsetsL1I> <bsizeL1I> <assocL1I> <nsetsL1D> <bsizeL1D> <assocL1D> <nsetsL2U> <bsizeL2U>  <assocL2U> <path> <SplitAddress>`

**SplitAddress** - Divisão de o que vai para a cache de dados (menor ou igual) e o que vai para a cache de instruções (maior)

**Path** - Caminho para o arquivo de entrada deve ser (*txt*)

**Exemplo:**  `java Processor 64 1 4 64 1 4 256 1 4 IO/arqTexto1_rw_10k.txt 500 ` 

##Mapeamentos

* Mapeamento direto - assoc = 1 e nsets > 1

* Mapeamento totalmente associativo - assoc > 1 e nsets = 1

* Mapeamento associativo por conjunto - assoc > 1 e nsets > 1
