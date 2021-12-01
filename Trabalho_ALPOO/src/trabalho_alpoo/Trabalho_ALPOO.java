/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho_alpoo;

import jandl.wizard.Data;
import jandl.wizard.WizardBase;
import jandl.wizard.WizardFactory;
import jandl.wizard.WizardText;
import java.sql.*;
import javax.swing.*; // Botão importado

/**
 *
 * @author igorm
 */
public class Trabalho_ALPOO {
    
    //CONEXÃO COM BANCO DE DADOS------------------------------------------------
    private static String dbDriver = "com.mysql.cj.jdbc.Driver";
    
    private static String dbUrl = "jdbc:mysql://localhost/trabalho_alpoo"
           + "?useTimezone=true&serverTimezone=UTC&useSSL=false";
    
    private static String dbUser = "root"; 
    
    private static String dbPwd = "";     
    //--------------------------------------------------------------------------
    //Pega o codEmpresa para ser usado mais tarde
    private static Integer codEmpresa;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        // janela UM                                  título  
        WizardBase janela1 = WizardFactory.createBase("Imagem",
                "TelaPrincipal.jpeg");
        
        
        //Criação dos campos das telas de inserção de dados-----------------------------------------------------
        
        //STRINGS PARA A JANELA 2, TEM QUE SER EM MERDA DE ARRAY
        //Nome dos campos
        String[] tagsEmpresa = {"txtCNPJ", "txtRazaoSocial"};
        //Texto dos Labels
        String[] labelsEmpresa = {"Digite o CNPJ da Empresa", "Digite o nome da Empresa"};
        //Dica do que preencher nos campos
        String[] tipsEmpresa = {"CNPJ da sua empresa", "Nome da sua empresa"};
        
        //Nome dos campos Loja
        String[] tagsLoja = {"txtValorVenda","txtQuantidade","txtDesc"};
        //Texto dos Labels LOJA
        String[] labelsLoja = {"Digite o valor da venda","Digite a quantidade vendida","Descrição do Produto"};
        //Dicas Loja
        String[] tipsLoja = {"Valor da venda","",""};
        
        //-----------------------------------------------------------------------------------------------------
        
//Armazena a imagem lateral
        String imgLateral = "TelaLateral.jpeg";
        
        
        // janela DOIS
        WizardBase janela2 = WizardFactory.createField("Formulário Empresa",tagsEmpresa, tipsEmpresa, labelsEmpresa);
        // JANELA TRÊS RESULTADOS PARCIAIS
        WizardBase janela3 = WizardFactory.createText("Resultados","!/resources/tux-luke-skywalker.jpg", true);
        
        
        //JANELA 4 PARA INSERIR OS DADOS DA LOJA
        WizardBase janela4 = WizardFactory.createField("Formulario Loja",tagsLoja, tipsLoja,labelsLoja);
        
        //Janela 5 RESULTADO FINAL
        WizardBase janela5 = WizardFactory.createText("Resultados Finais","!/resources/tux-luke-skywalker.jpg" , true);
        
        //JOptionPane.showMessageDialog(janela5, args, title, 0, icon);
        
        // encadeamento janela1 --> janela2 --> janela3 --> janela4
        janela1.nextWizard(janela2).nextWizard(janela3).nextWizard(janela4).nextWizard(janela5);
        
        //janela1.addPostProcessor(processor);//Eu adicionei essa porra agr
        
        // Pós e Pré processamento
        janela2.addPostProcessor((wiz)->janelaGeralPostProcessor(wiz));
        janela2.addPostProcessor((wiz)->InserirdadosEmpresa());
        janela3.addPreProcessor((wiz)->MostrarResultadoEmpresa(wiz));
        janela3.addPostProcessor((wiz)->janelaGeralPostProcessor(wiz));
        janela4.addPostProcessor((wiz)->InserirDadosLoja());
        janela4.addPostProcessor((wiz)->Inserir_TabelaFinal());
        janela5.addPreProcessor((wiz)->MostrarResultadoFinal(wiz));
        
        janela2.setImage("TelaLateral.png");
        janela3.setImage("TelaLateral.png");
        janela4.setImage("TelaLateral.png");
        janela5.setImage("TelaLateral.png");
        // Ativar a aplicação
        SwingUtilities.invokeLater(()-> janela1.setVisible(true));
        
        
    }
    //Validar se falta algum texto
     private static void janelaGeralPostProcessor(WizardBase wiz) {
        System.out.println("PostProcessor de " + wiz.getName());
    }

    private static void MostrarResultadoEmpresa(WizardBase wiz) {
        Data data = Data.instance();
        String cnpj = data.getAsString("Wizard2.fieldPane0.txtCNPJ");
        String nome = data.getAsString("Wizard2.fieldPane0.txtRazaoSocial");
        
        try{
            Class.forName(dbDriver);
                    
            Connection con = DriverManager.getConnection(dbUrl,dbUser,dbPwd);

            Statement stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            String sql = "SELECT CodEmpresa,CNPJ,RazaoSocial,DataInsercaoEmpresa FROM Empresa WHERE CNPJ = " + cnpj;

            ResultSet rs= stm.executeQuery(sql);
            rs.first();
            
            WizardText wt = (WizardText)wiz;
            wt.setText("Resultados da Consulta de acordo com o CNPJ : \n\n");
            wt.append("Código da Empresa = " + rs.getInt(1) + "\n"
            + rs.getString(2) + "\n" + rs.getString(3) + "\n" + 
                    rs.getString(4) + "\n");
            wt.append("============================");
            
            codEmpresa = rs.getInt(1);
            
        }
        catch(ClassNotFoundException | SQLException ex)
        {
            JOptionPane.showMessageDialog(null,"Erro : " + ex.getMessage());
        }
    }
    private static void MostrarResultadoFinal(WizardBase wiz) {
        Data data = Data.instance();
        String cnpj = data.getAsString("Wizard2.fieldPane0.txtCNPJ");
        
        try{
            Class.forName(dbDriver);
                    
            Connection con = DriverManager.getConnection(dbUrl,dbUser,dbPwd);

            Statement stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            String sql = "SELECT CodEmpresa,CNPJ,RazaoSocial,DataInsercaoEmpresa,CodVenda,ValorVenda,Quantidade,"
                    + "DescProduto,DataVenda FROM AmostraFinal Order By DataVenda DESC Limit 1";

            ResultSet rs= stm.executeQuery(sql);
            rs.next();
            
            WizardText wt = (WizardText)wiz;
            System.out.println(cnpj);
            wt.setText("Dados sobre a venda da empresa : \n\n");
            wt.append("Código da Empresa : " + rs.getInt(1) + "\nCNPJ: "
            + rs.getString(2) + "\nRazaoSocial: " + rs.getString(3) + "\nData de inserção :" + 
                    rs.getString(4) + "\n");
            wt.append("Código da Venda = " + rs.getInt(5) + "\n Valor da venda: "
            + rs.getDouble(6) + "\n Quantidade : " + rs.getInt(7) + "\nDescrição do produto: " + rs.getString(8) + "\nData de venda: " + 
                    rs.getString(9) + "\n");
            wt.append("============================");
            
        }
        catch(ClassNotFoundException | SQLException ex)
        {
            JOptionPane.showMessageDialog(null,"Erro : " + ex.getMessage());
            System.out.println(ex);
            ex.printStackTrace();
        }
    }
    
    
    private static void InserirdadosEmpresa(){
        try
        {
            Class.forName(dbDriver);
                    
            Connection con = DriverManager.getConnection(dbUrl,dbUser,dbPwd);

            String sql = "INSERT INTO Empresa(CNPJ,RazaoSocial)VALUES(?,?)";

            PreparedStatement prepStm = con.prepareStatement(sql);
            
            Data data = Data.instance();
            String cnpj = data.getAsString("Wizard2.fieldPane0.txtCNPJ");
            String nome = data.getAsString("Wizard2.fieldPane0.txtRazaoSocial");
            //Indica a posição de cada ponto de interrogação os números
            prepStm.setString(1, cnpj);
            prepStm.setString(2, nome);
            
            //Dá um update em meu comando com os valores passados
            prepStm.executeUpdate(); 

            JOptionPane.showMessageDialog(null, "Dados Inseridos com Sucesso!");

            con.close();

            prepStm.close();                 
         }
         catch(ClassNotFoundException | SQLException ex)
         {
             JOptionPane.showMessageDialog(null,"Erro: " + ex.getMessage());                     
         }                 
    }
    private static void InserirDadosLoja(){
        try
        {
            Class.forName(dbDriver);
            
            Connection con = DriverManager.getConnection(dbUrl,dbUser,dbPwd);
            
            String sql = "INSERT INTO Venda(CodEmpresa,ValorVenda,Quantidade,DescProduto)VALUES(?,?,?,?)";
            
            PreparedStatement prepStm = con.prepareStatement(sql);
            
            Data data = Data.instance();
            Double valor = data.getAsDouble("Wizard4.fieldPane0.txtValorVenda");
            Integer qtd = data.getAsInteger("Wizard4.fieldPane0.txtQuantidade");
            String desc = data.getAsString("Wizard4.fieldPane0.txtDesc");
            
            prepStm.setInt(1, codEmpresa);
            prepStm.setDouble(2, valor);
            prepStm.setInt(3, qtd);
            prepStm.setString(4, desc);
            
            prepStm.executeUpdate(); 

            JOptionPane.showMessageDialog(null, "Dados Inseridos com Sucesso!");

            con.close();

            prepStm.close();                 
        }
        catch(ClassNotFoundException | SQLException ex)
        {
            JOptionPane.showMessageDialog(null,"Erro : " + ex.getMessage());
        }
    }
    
    private static void Inserir_TabelaFinal()
    {
        try
        {
            Class.forName(dbDriver);
            
            Connection con = DriverManager.getConnection(dbUrl,dbUser,dbPwd);
            
            String sql = "INSERT INTO AmostraFinal(CodEmpresa,CNPJ,RazaoSocial,DataInsercaoEmpresa" +
                    ",CodVenda,ValorVenda,Quantidade,DescProduto,DataVenda) SELECT " +
                    "Empresa.CodEmpresa,CNPJ,RazaoSocial,DataInsercaoEmpresa,CodVenda,"
                    + "ValorVenda,Quantidade,DescProduto,DataVenda FROM Empresa,Venda";
            
            PreparedStatement prepStm = con.prepareStatement(sql);
            
            
            prepStm.executeUpdate();
            
            con.close();

            prepStm.close();                 
        }
        catch(ClassNotFoundException | SQLException ex)
        {
            JOptionPane.showMessageDialog(null,"Erro : " + ex.getMessage());
            System.out.println(ex);
            ex.printStackTrace();
        }
    }
    
    
    
}
