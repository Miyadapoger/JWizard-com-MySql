CREATE DATABASE Trabalho_ALPOO;

USE Trabalho_ALPOO;

CREATE TABLE Empresa(
	CodEmpresa INT PRIMARY KEY AUTO_INCREMENT,
    CNPJ VARCHAR(14),
    RazaoSocial VARCHAR(50),
    DataInsercaoEmpresa TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Venda(
	CodVenda INT PRIMARY KEY AUTO_INCREMENT,
    CodEmpresa INT,
    ValorVenda DOUBLE,
    Quantidade int,
    DescProduto VARCHAR(100),
    DataVenda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fkCNPJ FOREIGN KEY (CodEmpresa)  REFERENCES Empresa(CodEmpresa) 
);

CREATE TABLE AmostraFinal(
	CodEmpresa INT , 
    CodVenda INT ,
	CNPJ VARCHAR(14),
    RazaoSocial VARCHAR(50),
    DataInsercaoEmpresa TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	ValorVenda int,
    Quantidade int,
    DescProduto VARCHAR(100),
    DataVenda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fkCodEmpresa FOREIGN KEY (CodEmpresa)  REFERENCES Empresa(CodEmpresa) ,
    CONSTRAINT fkCodVenda FOREIGN KEY (CodVenda) REFERENCES Venda(CodVenda)
);



