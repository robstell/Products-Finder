//    System.out.println("Aperte qualquer tecla..."); Scanner in = new Scanner (System.in); in.nextLine();

package pacote_products_find;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.jsoup.parser.Parser;




public class Products_Find {
    
    
    

    
    public static String linkInicio = "https://www.estantevirtual.com.br/busca";
    public static String linkFim = "&b_order=data_cadastro";
    public static String palavra;
    public static String busca;
    public static String queryQ = "?q=";
    public static String queryED = "?qed=";
    
    public static ArrayList<String> listaPalavras = new ArrayList<>();
    public static ArrayList<ArrayList<String>> arquivadorListas = new ArrayList<>();
    public static ArrayList<String> listaSelecionada = new ArrayList<>();
    public static ArrayList<String> listaPersistente_Leitura = null;
    
    public static Path caminho = Paths.get("H:\\Backup\\Meus documentos\\Programas\\BuscaLivros\\listaPersistente.file");
    public static Path palavrasTXT = Paths.get("H:\\Backup\\Meus documentos\\Programas\\BuscaLivros\\palavras.txt");
    
    public static Document pagina_HTML_Capturada;
    
    public static String assunto = "Estante Virtual: ";
    
    public static int links = 0;
    
    private static ObjectOutputStream output;
    private static ObjectInputStream input;
    
    private static Scanner inputTXT;
    
    //public static String diaDaSemana = LocalDate.now().getDayOfWeek().name();

    
    
    
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
    
        
        
        leituraArquivoTXT();
        
        for (String i : listaPalavras){                                         //percorre lista de PALAVRAS BUSCADAS
            
            
            
            
            System.out.println(i.replace("?q=","").replace("?qed=",""));        //Exibe PALAVRA BUSCADA
            
            
                    
            
            
            
            formataPalavra(i);                                                  //Pega PALAVRA BUSCADA, constrói ENDEREÇO DE BUSCA.
            
            
            
            
            
            
            //System.out.println(busca);
            
            capturaPagina_HTML(busca);
            
            capturaLinks(pagina_HTML_Capturada);                                //Retorna ARQUIVADOR, cada posição corresponde ao total de links de cada PALAVRA BUSCADA
            
            
            
         }                                                                      
        
        
        
        selecionaLinks(arquivadorListas);                                       //seleciona os links de acordo com critério escolhido, retorna listaSelecionada
        
        
       
       
        
        //for (String i : listaSelecionada){
        //    System.out.println(i);
        //}
        
        
        enviaEmail(listaSelecionada);
        
        

     }
    
    
    
    
    
    public static Document capturaPagina_HTML(String link) throws IOException{
        
        pagina_HTML_Capturada = Jsoup.connect(link).get();
        
     return pagina_HTML_Capturada;
    }
    
    
    
    
    
    

    
    
    
    public static int randomizar (int min, int max){

        Random random = new Random();
        
     return random.nextInt ((max-1 - min ) + 1) + min;
     }
    
    
    
    
    
    
    
    
    public static String formataPalavra (String palavra){
        
        
        String palavraFormatada;
        
        if (palavra.contains(" ")){

            palavraFormatada = palavra.replace(" ","%20").replace("&","%26");     //Adiciona "%20" e "%26" aos espaços

         }
        else{

            palavraFormatada = palavra;

         }

        busca = linkInicio + palavraFormatada + linkFim;

        
        
        
        return busca;
     }
    
    
    
    
    
    
    public static ArrayList<ArrayList<String>> capturaLinks(Document pagina_HTML_Capturada) throws IOException {
        //    System.out.println("Aperte qualquer tecla..."); Scanner in = new Scanner (System.in); in.nextLine();
        
        
        
        ArrayList<String> listaLinks = new ArrayList<>();                       //Criação de LISTA DE LINKS
        
        int cont_paginador;                                                     //Declaração de CONTADORES DE PÁGINA
        
        int pag = 1;
        
        String buscaNextPage = null;
                       
        
        
        
        
        
        do{                                                                     
            
            
            
            
            cont_paginador = 0;
            
            if (pagina_HTML_Capturada.location().contains("&sugestao=1") ){                     //Caso contenha essa linha no ENDEREÇO, INTERROMPE o laço
                
                
                System.out.println("SEM RESULTADOS!"); //System.out.println("");                  
                
                break;
                
                
             }
            
            
            
            
            
            Elements linkElementsPaginador = pagina_HTML_Capturada.select(".pg-footer .paginas-disponiveis li");      //Captura PAGINADOR  
            
            for (Element i : linkElementsPaginador){

                cont_paginador++;                                                           //Faz CONTAGEM de elementos do PAGINADOR
                    
             }

           //System.out.println("Paginador: " + cont_paginador);
                
             
            
            
            
            if (pag != 1){                                                      //Verificador de PRIMEIRA PÁGINA
                                                                               
                System.out.println("página " + pag);                            //Caso seja OUTRA PÀGINA, exibe o número dela
                
                pagina_HTML_Capturada = capturaPagina_HTML(buscaNextPage);
                
             }
            
            
                           
            
            
            
            Elements linkElements = pagina_HTML_Capturada.select("div.livro");         //Captura LINKS
            
            String link;
            Elements capa;
            Elements titulo_autor;
            Elements precos;
            Elements quantidades;
            
            for (Element i : linkElements){
                
                
                
                link = i.select("a").attr("href");
                
                i.select("a.livro meta").remove();
                

                
//                i.select(".capa").wrap("<a href=" + link + " class=\"livro livro-capa\"></a>");                         //Criação de NOVAS CLASSES para facilitar manuseio
//                i.select(".titulo-autor").wrap("<a href=" + link + " class=\"livro livro-titulo-autor\"></a>");
//                i.select(".precos").wrap("<a href=" + link + " class=\"livro livro-precos\"></a>");
//                i.select(".quantidades").wrap("<a href=" + link + " class=\"livro livro-quantidades\"></a>");
                
                i.select(".capa").wrap("<div class=\"livro livro-capa\"></div>");                         //Criação de NOVAS CLASSES para facilitar manuseio
                i.select(".titulo-autor").wrap("<div class=\"livro livro-titulo-autor\"></div>");
                i.select(".precos").wrap("<div class=\"livro livro-precos\"></div>");
                i.select(".quantidades").wrap("<div class=\"livro livro-quantidades\"></div>");
                
                
                
//                capa = i.select("a.livro-capa");                                                                        //Captura de DADOS
//                titulo_autor = i.select("a.livro-titulo-autor");    
//                precos = i.select("a.livro-precos");
//                quantidades = i.select("a.livro-quantidades");
                
                capa = i.select("div.livro-capa");                                                                        //Captura de DADOS
                titulo_autor = i.select("div.livro-titulo-autor");    
                precos = i.select("div.livro-precos");
                quantidades = i.select("div.livro-quantidades");
                
                
                

                    
                
                
                
                
                //System.out.println(assunto);
                
                //System.out.println("Aperte qualquer tecla..."); Scanner in = new Scanner (System.in); in.nextLine();
                
                
                
                String table = 
                "    <a href=" + link + " class=\"livro\">\n" +    
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +                       //Criação de TABELA para formatação e exibição
                "        <tr>\n" +
                "            <td style=\"width: 100px;\">\n" +
                "                " + capa + "\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                " + titulo_autor + "\n" +
                "            </td>\n" +
                "            <td>\n" +
                "                " + precos + "\n" +
                "                " + quantidades + "\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table></a>";
                
                
                
                //System.out.println(table);
                //System.out.println("Aperte qualquer tecla..."); Scanner in = new Scanner (System.in); in.nextLine();
                
                
                
                listaLinks.add(table.replace("data-src","src"));                                                        //Adiciona tabela formatada à LISTA DE LINKS
                
                links++;                                                                                                //Contagem de LINKS TOTAIS
                
                
                
             }
            
            //System.out.println("Disponíveis: "+ listaLinks.size() + "  |  Capturados: " + links); System.out.println("");       //Exibição de LINKS DISPONÍVEIS e TOTAL CAPTURADOS
            
            
            
            
            
            pag++;                                                                                                              //Avança CONTADOR DE PÁGINA...
            
            buscaNextPage = busca + "&offset=" + pag;                                                                           //...criação de ENDEREÇO DE BUSCA da nova página
            
            
            
         
            
         }
        while ((cont_paginador == 3) || (cont_paginador == 2 && pag == 2));                                                                                            //Enquanto o PÁGINADOR tiver 3 elementos, CONTINUA
        
        
        
        
        arquivadorListas.add(listaLinks);                                                                                   //Adiciona LISTA DE LINKS ao ARQUIVADOR DAS LISTAS
        
        System.out.println("Disponíveis: "+ listaLinks.size() + "  |  Capturados: " + links); System.out.println("");       //Exibição de LINKS DISPONÍVEIS e TOTAL CAPTURADOS
        
        
        
        
     return arquivadorListas;   
    }
    
    
    
    
    
    
    
    
    public static ArrayList<String> selecionaLinks(ArrayList<ArrayList<String>> arquivadorListas) throws ClassNotFoundException, IOException{
        
        
        
        ArrayList<String> listaLinks;                                           //Criação de LISTA DE LINKS
        
        ArrayList<String> listaSelecionada2 = new ArrayList<>();
        
        int qtdResultados = 10;                                                 //Quantidade de RESULTADOS mínimos
        
        int qtdLinksSelecionados = 1;                                           //Quantidade de LINKS a serem SELECIONADOS
        
        int qtdLinksEmail = 33;                                                 //Quantidade de LINKS a serem EXIBIDOS NO EMAIL
       
        
        
//        
//        if (diaDaSemana.equals("MONDAY")){
//             
//         
//            Files.deleteIfExists(caminho);
//            System.out.println("Arquivo apagado!");
//           
//         }
        
        leituraLista(); System.out.println("");
        
        
        
        
        
        
        
        
        
        for (int i=0; i < arquivadorListas.size(); i++ ){                       //Repetição até terminar os links no ARQUIVADOR
            
            
            
            
            
            listaLinks = arquivadorListas.get(i);                               //Jogando conteúdo da posição X (todos os resultados de 1 PALAVRA BUSCADA) em um atributo 
            
            
            //System.out.println("");
            //System.out.println("Tamanho da lista: " + listaLinks.size());
            
                
             
                
            if (listaLinks.size() >= qtdResultados){                             //Se tiverem MAIS de X resultados...
                
                
                
                //System.out.println("Lista: " + i+1);
                
                for (int it=0; it < qtdLinksSelecionados; it++ ){               //Captura apenas 3 resultados e...
                    
                    
                    if (Files.exists(caminho)){
                        
                        
                        Element element = Jsoup.parse(listaLinks.get(it), "", Parser.htmlParser());
                        
                        String url = element.select("a.livro").attr("href");
                        
                        if (!listaPersistente_Leitura.contains(url)){

                            listaSelecionada.add(listaLinks.get(it));                   //...adiciona na LISTA SELECIONADA.
                            //System.out.println(it+1 + ") " + listaLinks.get(it));
                            
                         }
                            
                    
                    
                     }
                    else{
                      
                        
                        listaSelecionada.add(listaLinks.get(it));                   //...adiciona na LISTA SELECIONADA.
                        //System.out.println(it+1 + ") " + listaLinks.get(it));
                        
                        
                        
                     }
                    
                    
                    
                    
                    
                    

                 }
                
                
                               
                for (int it=qtdLinksSelecionados; it < listaLinks.size(); it++ ){               //Captura os outros resultados e...


                    listaSelecionada2.add(listaLinks.get(it));                   //...adiciona na LISTA SELECIONADA 2.
                    //System.out.println(it+1 + ") " + listaLinks.get(it));


                 }
                
                

                 
             }
            else{
                
                
                for (int it=0; it < listaLinks.size(); it++ ){               //Captura apenas 3 resultados e...


                    listaSelecionada2.add(listaLinks.get(it));                   //...adiciona na LISTA SELECIONADA.
                    //System.out.println(it + ") " + listaLinks.get(it));


                 }
                
                
             }
            
            
            
         }
        
        
        
        
        
        Collections.shuffle(listaSelecionada);                          //Randomiza os resultados da LISTA SELECIONADA.
        
        Collections.shuffle(listaSelecionada2);
        
        
        System.out.println("Links Novos: " + listaSelecionada.size());
        
        System.out.println("Links Selecionados: " + listaSelecionada2.size());
       
        
        
        
        
        int tamanhoDisponivelArray = qtdLinksEmail - listaSelecionada.size();
        
        System.out.println("Tamanho Disponível no array: " + tamanhoDisponivelArray);
        
        System.out.println("Tamanho da Lista Arquivada: " + listaPersistente_Leitura.size());
        
        
        
        
        
        
        int qtdIneditos = 0;
        
        for (String i : listaSelecionada2){
            
            
            Element element = Jsoup.parse(i, "", Parser.htmlParser());
                        
            String url = element.select("a.livro").attr("href");
            
            if (!listaPersistente_Leitura.contains(url)){
                
                qtdIneditos++;
                
             }
            
         }
        
        System.out.println("Quantidade de Links Inéditos: " + qtdIneditos); System.out.println("");
        
        
        
        
        
        
        
        
        
        Integer[] array = new Integer [tamanhoDisponivelArray];                              //Criação de ARRAY de 3 posições

        List<Integer> listaArray = Arrays.asList(array);               //Transformação do ARRAY criado anteriormente em LISTA
        
        
        
        
        for (int it=0; it < tamanhoDisponivelArray; it++ ){               //Pega ALEATÓRIAMENTE outros 3 links de cada PALAVRA BUSCADA



            
            int numAleatorio = randomizar(0, listaSelecionada2.size());
            
            //System.out.println(numAleatorio);
            
            
            
            
            //System.out.println("Aperte qualquer tecla..."); Scanner in = new Scanner (System.in); in.nextLine();
            
            
            if (Files.exists(caminho)){
                
                
                
                
                if(it < qtdIneditos){
                    
                    
                    Element element = Jsoup.parse(listaSelecionada2.get(numAleatorio), "", Parser.htmlParser());
                        
                    String url = element.select("a.livro").attr("href");
                    
                    while ((listaArray.contains(numAleatorio)) || (listaPersistente_Leitura.contains(url))){



                        //System.out.println("denovo");

                        numAleatorio = randomizar(0, listaSelecionada2.size());

                        //System.out.println(numAleatorio);
                        //System.out.println(listaSelecionada2.get(numAleatorio));

                        //System.out.println("Aperte qualquer tecla..."); Scanner in = new Scanner (System.in); in.nextLine();
                        
                        
                        element = Jsoup.parse(listaSelecionada2.get(numAleatorio), "", Parser.htmlParser());
                        
                        url = element.select("a.livro").attr("href");


                     }
                    
                    
                    
                    
                 
                 }
                else{
                    
                    
                    
                    
                    while (listaArray.contains(numAleatorio)){



                        //System.out.println("denovo");

                        numAleatorio = randomizar(0, listaSelecionada2.size());

                        //System.out.println("Aperte qualquer tecla..."); Scanner in = new Scanner (System.in); in.nextLine();


                        //System.out.println(numAleatorio);





                     }
                    
                    
                    
                    
                 }
                
                
                
                
                
                
                
             }
            else{
                
                
                
                while (listaArray.contains(numAleatorio)){



                    //System.out.println("denovo");

                    numAleatorio = randomizar(0, listaSelecionada2.size());

                    //System.out.println("Aperte qualquer tecla..."); Scanner in = new Scanner (System.in); in.nextLine();


                    //System.out.println(numAleatorio);
                
                
                
                

                 }
                
                
                
                
             }
            

            

            array[it] = numAleatorio;
            //System.out.println(numAleatorio + ") " + listaSelecionada2.get(numAleatorio));
            //System.out.println(listaArray); System.out.println("");
           
                 
                    
            listaSelecionada.add(listaSelecionada2.get(numAleatorio));
            
            
            
                     
            
            
            
            



         }
        
        
        
        
        
        

        
       
        
        //        if (listaPersistente_Leitura != null){
        //            
        //            for (String i : listaSelecionada){
        //                
        //                
        //                if (!listaPersistente_Leitura.contains(i)){
        //                 
        //                    listaPersistente_Leitura.add(i);
        //                 
        //                 }
        //            
        //             }
        //            
        //            gravarLista(listaPersistente_Leitura);
        //            
        //         }
        //        else{
        //            
        //            gravarLista(listaSelecionada);
        //            
        //         }
        
        
        
         
           
            
        

        int cont=0;
        for (String i : listaSelecionada){


            Element element = Jsoup.parse(listaSelecionada.get(cont), "", Parser.htmlParser());

            String url = element.select("a.livro").attr("href");

            if ((listaPersistente_Leitura != null) && (!listaPersistente_Leitura.contains(url))){

                listaPersistente_Leitura.add(url);

             }
            else{
                
                
                listaPersistente_Leitura.add(url);
                
             }
            
            

            cont++;
            
            

         }

        gravarLista(listaPersistente_Leitura);

        
        
        
        
        
        
        
         
        
        
        
        
        return listaSelecionada;
     }
    
    
    
    
    
    
    
    
    public static void enviaEmail (ArrayList<String> listaSelecionada) throws UnsupportedEncodingException{
         
        
        
        
        System.out.println("Enviando E-mail...");
        
        
        
        
        int qtdAssuntos = 3;
        
        for (int cont = 0; cont < qtdAssuntos; cont ++){
            
            
            Element element = Jsoup.parse(listaSelecionada.get(cont), "", Parser.htmlParser());
            
            
      //      if (cont != qtdAssuntos-1){                                         //Não funciona, preciso olhar melhor
      //         
      //          assunto += element.select("h2").attr("title") + " / ";
      //          converterTitleCase(assunto);
      //       }
      //      else{
             
      //          assunto += element.select("h2").attr("title");
      //          converterTitleCase(assunto);
      //       }

                 
         }
        
        
        
        
        Properties props = new Properties();
        /** Parâmetros de conexão com servidor Gmail */
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication("USER", "PASSWORD");
            }
        });

        /** Ativa Debug para sessão */
        session.setDebug(false);

        try {

          Message message = new MimeMessage(session);
          message.setFrom(new InternetAddress("asiloatomico@gmail.com", "Asilo Atômico"));   //Remetente

          Address[] toUser = InternetAddress.parse("DESTINATION");  //Destinatário(s)

          message.setRecipients(Message.RecipientType.TO, toUser);
          
          message.setSubject(assunto);  //Assunto
          
          
          
          String headerHTML =   
            "<!DOCTYPE html>\n" +
            "<html lang=\"pt-br\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Document</title>\n" +
            "\n" +
            "<style>\n" +
            "    body{\n" +
            "      font-family: Source Sans Pro,sans-serif;\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "    a.livro, a:link, a:visited{\n" +
            "      text-decoration:none;\n" +
            "      color:rgb(65, 67, 70);\n" +
            "	  }\n" +
            "\n" +
            "    .origem-capa, .ver-livros{\n" +
            "      display:none;\n" +
            "    }\n" +
            "       \n" +
            "    \n" +
            "    table{\n" +
            "        border: 1px solid #ccc;\n" +
            "        border-radius: .3125rem;\n" +
            "        padding: .625rem 0rem .9375rem 0rem;\n" +
            "        margin-bottom: .625rem;\n" +
            "        text-decoration: none;\n" +
            "        \n" +
            "    }\n" +
            "\n" +
            "    .capa img{\n" +
            "      display: block;\n" +
            "      width: 100%;\n" +
            "      height: 6.125rem;\n" +
            "      object-fit: contain;\n" +
            "    }\n" +
            "\n" +
            "    .capa{\n" +
            "      grid-area: capa;\n" +
            "      /*display: -webkit-flex;\n" +
            "      display: -ms-flexbox;*/\n" +
            "      display: flex;\n" +
            "      /*-webkit-align-items: center;\n" +
            "      -ms-flex-align: center;*/\n" +
            "      align-items: center;\n" +
            "      /*-webkit-flex-grow: 0;\n" +
            "      -ms-flex-positive: 0;*/\n" +
            "      flex-grow: 0;\n" +
            "      align-self: center;\n" +
            "      /*-webkit-justify-content: center;\n" +
            "      -ms-flex-pack: center;*/\n" +
            "      justify-content: center;\n" +
            "    }\n" +
            "\n" +
            "    .titulo-autor{\n" +
            "      grid-area: titulo;\n" +
            "      text-align: left;\n" +
            "      /*display: -webkit-flex;\n" +
            "      display: -ms-flexbox;*/\n" +
            "      display: block;\n" +
            "      /*-webkit-flex-direction: column;\n" +
            "      -ms-flex-direction: column;*/\n" +
            "      flex-direction: column;\n" +
            "      /*-webkit-justify-content: center;\n" +
            "      -ms-flex-pack: center;*/\n" +
            "      justify-content: center;\n" +
            "    }\n" +
            "\n" +
            "    .titulo-autor h2{\n" +
            "        display: block;\n" +
            "        max-width: 11.25rem;\n" +
            "        font-size: .9375rem;\n" +
            "        font-weight: 600;\n" +
            "        margin: 0;\n" +
            "      }\n" +
            "\n" +
            "    .titulo-autor span{\n" +
            "      display: block;\n" +
            "      color: #449aaa;\n" +
            "      font-size: .8125rem;\n" +
            "      font-weight: 400;\n" +
            "    }\n" +
            "    \n" +
            "    .precos{\n" +
            "      text-align: right;\n" +
            "      padding: 0 10px 0 0 ;\n" +
            "      grid-area: preco;\n" +
            "      /*display: -webkit-flex;\n" +
            "      display: -ms-flexbox;*/\n" +
            "      /*display: flex/*;\n" +
            "      /*text-align: center;\n" +
            "      -webkit-align-items: center;\n" +
            "      -ms-flex-align: center;*/\n" +
            "      align-items: center;\n" +
            "      /*-webkit-justify-content: center;\n" +
            "      -ms-flex-pack: center;*/\n" +
            "      justify-content: center;\n" +
            "      font-size: .75rem;\n" +
            "    }\n" +
            "    \n" +
            "    .preco{\n" +
            "      font-weight: 700;\n" +
            "    }\n" +
            "\n" +
            "    .quantidades[data-v-3f1b0fca] {\n" +
            "      margin: 10px 0;\n" +
            "    }\n" +
            "    \n" +
            "    .quantidades{\n" +
            "      grid-area: quant;\n" +
            "      font-size: .875rem;\n" +
            "      /*display: -webkit-flex;\n" +
            "      display: -ms-flexbox;*/\n" +
            "      /*display: flex;*/\n" +
            "      /*-webkit-justify-content: center;\n" +
            "      -ms-flex-pack: center;*/\n" +
            "      justify-content: center;\n" +
            "      /*-webkit-align-items: center;\n" +
            "      -ms-flex-align: center;\n" +
            "      align-items: center;*/\n" +
            "      text-align: right;\n" +
            "      padding: 0 10px 0 0 ;\n" +
            "      /*-webkit-align-items: flex-start;\n" +
            "      -ms-flex-align: start;*/\n" +
            "      align-items: flex-start;\n" +
            "    }\n" +
            "\n" +
            "</style>\n" +
            "\n" +
            "</head>\n" +
            "<body>\n" +
            "    ";
          
          
             
          
         String footerHTML = "</body>\n" +
                            "</html>";



         String delimiter = "\n\n" ;
         String setBody = String.join(delimiter, listaSelecionada);
         String htmlMessage = String.join(delimiter, headerHTML, setBody, footerHTML);
         
         //System.out.println(htmlMessage);
         //System.out.println("Aperte qualquer tecla..."); Scanner in = new Scanner (System.in); in.nextLine();
         
         

         Multipart multipart = new MimeMultipart();

         MimeBodyPart attachment0 = new MimeBodyPart();
         attachment0.setContent(htmlMessage,"text/html; charset=UTF-8");
         multipart.addBodyPart(attachment0);

         message.setContent(multipart);
            
          
          
          /**Método para enviar a mensagem criada*/
          Transport.send(message);

          System.out.println("E-mail enviado!");

         } catch (MessagingException e) {
            throw new RuntimeException(e);
         }
     }
    
    
    
    
    
    
    
    
    public static void gravarLista(ArrayList<String> lista) throws IOException{
        
         
        output = new ObjectOutputStream(Files.newOutputStream(caminho));
        
        output.writeObject(lista);
        
        
        if (output != null){
            
            output.close();
            
         }
        
        System.out.println("Arquivo gravado!"); System.out.println("");
        
     }
    
    
    
    
    
    
    
    
    public static void leituraLista() throws ClassNotFoundException{
        
        
         try{
            
             
            
            input = new ObjectInputStream(Files.newInputStream(caminho));

            listaPersistente_Leitura = (ArrayList<String>) input.readObject();

            
            if (input != null){
                
                input.close();
                
             }
            

            System.out.println("Arquivo lido!");

            
            
         }
        
         catch (IOException ioException){
         
            System.err.println("Arquivo não existe!");            
            
         }
        
        
     }
    
    
    
    public static void leituraArquivoTXT() throws IOException{
        
        try{
            
            
            inputTXT = new Scanner(palavrasTXT);
        
            
            while (inputTXT.hasNextLine()){
                
                 
               
                String linhaCompleta = inputTXT.nextLine();
                
                
                
                if (!linhaCompleta.isEmpty() ) {
                   
                   
                    
                    
                    palavra = linhaCompleta.replace("||EDITORA","");
                
                
                    

                    if (linhaCompleta.contains("||EDITORA")){
                        

                        listaPalavras.add(queryED + palavra);
                        

                     }
                    else{
                        

                        listaPalavras.add(queryQ + palavra);
                        

                     }
                    
                    
                    
                    
                   
                 }
                
                
                
                
                
                
             }
            
            
            
            if (inputTXT != null){
                
                inputTXT.close();
                
             }
            
            
            //System.out.println("Lista de palavras lida!");
            
            
            
        
         }
        catch (IOException ioException){
            
            System.err.println("Arquivo não existe!");
            
         }
        
        
        
     }
    
    
    
    
    public static String converterTitleCase(String text) {
        
        String separador = " ";
        
        if (text == null || text.isEmpty()) {
            return text;
        }

        return Arrays
          .stream(text.split(separador))
          .map(word -> word.isEmpty()
            ? word
            : Character.toTitleCase(word.charAt(0)) + word
              .substring(1)
              .toLowerCase())
          .collect(Collectors.joining(separador));
    }
    
    
    
    
 }


