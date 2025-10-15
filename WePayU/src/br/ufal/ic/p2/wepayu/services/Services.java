package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.*;
import br.ufal.ic.p2.wepayu.models.*;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Services {
    private boolean sistemaEncerrado = false;
    public static Stack<Command> undo = new Stack<>();
    public static Stack<Command> redo = new Stack<>();
    private final String dados = "Persistence.xml";
    private HashMap<String, Empregado> empregados = new HashMap<>();
    public static List<String> agendasPagamento;


    int i = 0;

    public Services() throws Exception {
        this.agendasPagamento = new ArrayList<>();
        this.agendasPagamento.add("mensal $");
        this.agendasPagamento.add("semanal 2 5");
        this.agendasPagamento.add("semanal 5");

        carregar();
    }
    @SuppressWarnings("unchecked")
    private void carregar() throws Exception {
        File arquivo = new File(dados);
        if (!arquivo.exists()) {
            setEmpregados(new HashMap<>());
            return;
        }

        try (FileInputStream input = new FileInputStream(arquivo);
             XMLDecoder decodificador = new XMLDecoder(input)) {

            setEmpregados((HashMap<String, Empregado>) decodificador.readObject());

            try {
                this.agendasPagamento = (List<String>) decodificador.readObject();
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
    }
    private void salvar() throws Exception {
        try (FileOutputStream output = new FileOutputStream(dados);
             XMLEncoder codificador = new XMLEncoder(output)) {
            codificador.writeObject(getEmpregados());
            codificador.writeObject(this.agendasPagamento);
        }
    }

    public void encerrarSistema() throws Exception {
        salvar();
        this.sistemaEncerrado = true;
    }
    public boolean isSistemaEncerrado() {return this.sistemaEncerrado;}
    public void zerarSistema() {
        HashMap<String, Empregado> backup = new HashMap<>(getEmpregados());
        List<String> backupSindicato = new ArrayList<>(Sindicato.getIdsSindicato());
        List<String> backupAgendas = new ArrayList<>(this.agendasPagamento);

        Command c = new Command() {
            @Override
            public void executar() {
                zerarEmpregados();
                Sindicato.zerarSindicato();
                agendasPagamento = new ArrayList<>();
                agendasPagamento.add("mensal $");
                agendasPagamento.add("semanal 2 5");
                agendasPagamento.add("semanal 5");
            }
            @Override
            public void desfazer() {
                setEmpregados(backup);
                Sindicato.setIdsSindicato(backupSindicato);
                agendasPagamento = backupAgendas;
            }
        };
        c.executar();
        undo.push(c);
        redo.clear();
        this.sistemaEncerrado = false;
        File arquivo = new File(dados);
        if (arquivo.exists()) arquivo.delete();
    }

    public void undo() throws Exception {
        if(undo.isEmpty()) throw new UndoEmptyException();
        Command c = undo.pop();
        c.desfazer();
        redo.push(c);
    }
    public void redo() throws Exception{
        if(redo.isEmpty()) throw new RedoEmptyException();
        Command c = redo.pop();
        c.executar();
        undo.push(c);
    }

    public void zerarEmpregados() {empregados.clear();}
    public HashMap<String, Empregado> getEmpregados() {return empregados;}
    public void setEmpregados(HashMap<String, Empregado> empregados) {this.empregados = empregados;}
    public int getNumeroDeEmpregados() {return empregados.size();}



    public void criarAgendaDePagamentos(String descricao) throws Exception {
        if (agendasPagamento.contains(descricao)) {
            throw new ScheduledPaymentExceptionExists();
        }

        String[] agenda = descricao.split(" ");
        boolean agendaValida = false;
        String tipoAgenda = agenda[0];

        try {
            if (tipoAgenda.equals("semanal")) {
                if (agenda.length == 2) {
                    int dia = Integer.parseInt(agenda[1]);
                    if (dia >= 1 && dia <= 7) {
                        agendaValida = true;
                    }
                } else if (agenda.length == 3) {
                    int frequencia = Integer.parseInt(agenda[1]);
                    int dia = Integer.parseInt(agenda[2]);
                    if (frequencia >= 1 && frequencia <= 52 && dia >= 1 && dia <= 7) {
                        agendaValida = true;
                    }
                }
            } else if (tipoAgenda.equals("mensal")) {
                if (agenda.length == 2) {
                    String dia = agenda[1];
                    if (dia.equals("$")) {
                        agendaValida = true;
                    } else {
                        int diaDoMes = Integer.parseInt(dia);
                        if (diaDoMes >= 1 && diaDoMes <= 28) {
                            agendaValida = true;
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            agendaValida = false;
        }


        if (agendaValida) {
            agendasPagamento.add(descricao);
        } else {
            throw new Exception("Descricao de agenda invalida");
        }
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        if (nome.isEmpty()) throw new NameNullException();
        else if (endereco.isEmpty()) throw new AddressNullException();
        else if (!tipo.equals("assalariado") && !tipo.equals("comissionado") && !tipo.equals("horista")) throw new TypeInvalidException();
        else if (tipo.equals("comissionado")) throw new TypeNotUsedException();
        double salarioDouble = converterSalario(salario);

        String id = darId(i++);
        Empregado empregado;
        if (tipo.equals("assalariado")){
            empregado = new Assalariado(id, nome, endereco, tipo, salarioDouble);
            empregado.setAgendaPagamento("mensal $");
        }
        else {
            empregado = new Horista(id, nome, endereco, tipo, salarioDouble);
            empregado.setAgendaPagamento("semanal 5");
        }

        Command c = new Command() {
            @Override
            public void executar() {
                empregados.put(id, empregado);
            }
            @Override
            public void desfazer() {
                empregados.remove(id);
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();

        return id;
    }

    public String totalFolha(String data) throws Exception {
        HashMap<String, Empregado> empregados = this.empregados;
        LocalDate dataFolha = converterData(data);
        if (isAntigo(empregados)) return totalFolhaAntiga(dataFolha, empregados);
        else return novaTotalFolha(dataFolha, empregados);
    }

    private String novaTotalFolha(LocalDate dataFolha, HashMap<String, Empregado> empregados) {
        BigDecimal totalFolha = BigDecimal.ZERO;

        for (Empregado empregado : empregados.values()) {
            if (diaPagamento(empregado, dataFolha)) {
                String[] agenda = empregado.getAgendaPagamento().split(" ");
                boolean ehMensal = agenda[0].equals("mensal");
                int semanas = (!ehMensal && agenda.length > 1 && !agenda[1].equals("$")) ? (agenda.length == 3 ? Integer.parseInt(agenda[1]) : 1) : 1;

                LocalDate dataInicial = ehMensal
                        ? dataFolha.with(TemporalAdjusters.firstDayOfMonth())
                        : dataFolha.minusWeeks(semanas).plusDays(1);

                BigDecimal pagamentoPeriodo = BigDecimal.ZERO;

                if (empregado instanceof Assalariado || empregado instanceof Comissionado) {
                    BigDecimal salarioMensal = BigDecimal.valueOf(empregado.getSalario());
                    if (ehMensal) {
                        pagamentoPeriodo = salarioMensal;
                    } else {
                        pagamentoPeriodo = salarioMensal
                                .multiply(new BigDecimal("12"))
                                .multiply(new BigDecimal(semanas))
                                .divide(new BigDecimal("52"), 2, RoundingMode.DOWN);
                    }
                }

                if (empregado instanceof Horista) {
                    double[] horas = ((Horista) empregado).getHorasNoPeriodo(dataInicial, dataFolha);
                    BigDecimal salarioHora = BigDecimal.valueOf(empregado.getSalario());
                    BigDecimal horasNormais = BigDecimal.valueOf(horas[0]);
                    BigDecimal horasExtras = BigDecimal.valueOf(horas[1]);
                    BigDecimal pagamentoHorasNormais = horasNormais.multiply(salarioHora);
                    BigDecimal pagamentoHorasExtras = horasExtras.multiply(salarioHora).multiply(new BigDecimal("1.5"));
                    pagamentoPeriodo = pagamentoHorasNormais.add(pagamentoHorasExtras);
                } else if (empregado instanceof Comissionado) {
                    double totalVendas = ((Comissionado) empregado).getVendasNoPeriodo(dataInicial, dataFolha);
                    BigDecimal vendasBD = BigDecimal.valueOf(totalVendas);
                    BigDecimal comissaoTaxaBD = BigDecimal.valueOf(((Comissionado) empregado).getComissao());
                    BigDecimal comissaoValor = vendasBD.multiply(comissaoTaxaBD).setScale(2, RoundingMode.DOWN);
                    pagamentoPeriodo = pagamentoPeriodo.add(comissaoValor);
                }
                totalFolha = totalFolha.add(pagamentoPeriodo);
            }
        }
        return String.format("%.2f", totalFolha).replace('.', ',');
    }
    private String totalFolhaAntiga(LocalDate dataFolha, HashMap<String, Empregado> empregados) {
        BigDecimal totalFolha = BigDecimal.ZERO;
        for (Empregado empregado : empregados.values()) {
            if (empregado instanceof Horista horista && dataFolha.getDayOfWeek() == DayOfWeek.FRIDAY) {
                LocalDate dataInicial = dataFolha.minusDays(6);
                double[] horas = horista.getHorasNoPeriodo(dataInicial, dataFolha);
                double v = horas[0] * empregado.getSalario() + horas[1] * empregado.getSalario() * 1.5;
                totalFolha = totalFolha.add(BigDecimal.valueOf(v));            }
            else if (empregado instanceof Assalariado && isUltimoDiaUtil(dataFolha)) totalFolha = totalFolha.add(BigDecimal.valueOf(empregado.getSalario()));
            else if (empregado instanceof Comissionado comissionado && isDiaDePagamentoComissionado(dataFolha)) {
                LocalDate dataInicial = dataFolha.minusDays(13);
                double totalVendas = comissionado.getVendasNoPeriodo(dataInicial, dataFolha);
                BigDecimal salarioBD = BigDecimal.valueOf(empregado.getSalario());
                BigDecimal salarioFixoBD = salarioBD.multiply(new BigDecimal("12")).divide(new BigDecimal("26"), 2, RoundingMode.DOWN);
                BigDecimal totalVendasBD = BigDecimal.valueOf(totalVendas);
                BigDecimal comissaoTaxaBD = BigDecimal.valueOf(comissionado.getComissao());
                BigDecimal comissaoBD = totalVendasBD.multiply(comissaoTaxaBD).setScale(2, RoundingMode.DOWN);
                totalFolha = totalFolha.add(salarioFixoBD.add(comissaoBD));
            }
        }
        return String.format("%.2f", totalFolha).replace('.', ',');
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        if (nome.isEmpty()) throw new NameNullException();
        else if (endereco.isEmpty()) throw new AddressNullException();
        else if (!tipo.equals("assalariado") && !tipo.equals("comissionado") && !tipo.equals("horista")) throw new TypeInvalidException();
        else if (!tipo.equals("comissionado")) throw new TypeNotUsedException();
        double salarioDouble = converterSalario(salario);
        double comissaoDouble = converterComissao(comissao);

        String id = darId(i++);
        Empregado empregado = new Comissionado(id, nome, endereco, tipo, salarioDouble, comissaoDouble);
        empregado.setAgendaPagamento("semanal 2 5");

        Command c = new Command() {
            @Override
            public void executar() {
                empregados.put(id, empregado);
            }
            @Override
            public void desfazer() {
                empregados.remove(id);
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();

        return id;
    }

    public void removerEmpregado(String id) throws Exception {
        if (id.isEmpty()) throw new EmployeeNullException();
        Empregado backup = empregados.get(id);
        if (backup == null) throw new EmployeeNotFoundException();

        Command c = new Command() {
            @Override
            public void executar() {
                empregados.remove(id);
            }
            @Override
            public void desfazer() {
                empregados.put(id, backup);
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();
    }

    public String getAtributoEmpregado(String id, String atributo) throws Exception {
        if (id.isEmpty()) throw new EmployeeNullException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmployeeNotFoundException();
        Comissionado comissionado = new Comissionado();
        if (emp.getTipo().equals("comissionado")) comissionado = converterComissionado(id, ((Comissionado) emp).getComissao(), emp);
        return switch (atributo) {
            case "id" -> emp.getId();
            case "nome" -> emp.getNome();
            case "endereco" -> emp.getEndereco();
            case "tipo" -> emp.getTipo();
            case "salario" -> String.format("%.2f", emp.getSalario());
            case "comissao" -> {
                if (emp.getTipo().equals("comissionado")) yield String.format("%.2f", comissionado.getComissao());
                else throw new EmployeeNotCommissionedException();
            }
            case "sindicalizado" -> Boolean.toString(emp.isSindicalizado());
            case "metodoPagamento" -> emp.getMetodoPagamento();
            case "banco" -> {
                if (!emp.getMetodoPagamento().equals("banco")) throw new BankNotFoundException();
                yield emp.getBanco();
            }
            case "agencia" -> {
                if (!emp.getMetodoPagamento().equals("banco")) throw new BankNotFoundException();
                yield emp.getAgencia();
            }
            case "contaCorrente" -> {
                if (!emp.getMetodoPagamento().equals("banco")) throw new BankNotFoundException();
                yield emp.getContaCorrente();
            }
            case "idSindicato" -> {
                if (emp.isSindicalizado()) yield emp.getIdSindicato();
                else throw new EmployeeNotInSyndicateException();
            }
            case "taxaSindical" -> {
                if (emp.isSindicalizado()) yield String.format("%.2f", emp.getTaxaSindical());
                else throw new EmployeeNotInSyndicateException();
            }
            case "agendaPagamento" -> emp.getAgendaPagamento();

            default -> throw new AttributeNotFoundException();
        };
    }

    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        List<Empregado> nomesEmpregados = new ArrayList<>();
        for (Empregado empregado : empregados.values()) if (empregado.getNome().equals(nome)) nomesEmpregados.add(empregado);
        if (nomesEmpregados.isEmpty()) throw new NameNotFoundException();
        nomesEmpregados.sort(Comparator.comparing(Empregado::getId));
        return nomesEmpregados.get(indice - 1).getId();
    }

    public void alteraEmpregado(String id, String atributo, String valor) throws  Exception {
        if (id.isEmpty()) throw new EmployeeNullException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmployeeNotFoundException();
        Empregado backup;
        if (emp instanceof Horista) backup = new Horista((Horista) emp);
        else if (emp instanceof Comissionado) backup = new Comissionado((Comissionado) emp);
        else backup = new Assalariado((Assalariado) emp);
        switch (atributo) {
            case "nome" -> {
                if (valor == null || valor.isEmpty()) throw new NameNullException();
                else emp.setNome(valor);
            }
            case "endereco" -> {
                if (valor == null || valor.isEmpty()) throw new AddressNullException();
                else emp.setEndereco(valor);
            }
            case "tipo" -> {
                if (emp.getTipo().equals(valor)) return;
                switch (valor) {
                    case "comissionado" -> throw new CommissionNullException();
                    case "horista" -> {
                        Horista horista = converterHorista(id, emp);
                        empregados.put(id, horista);
                    }
                    case "assalariado" -> {
                        Assalariado assalariado = converterAssalariado(id, emp);
                        empregados.put(id, assalariado);
                    }
                    default -> throw new TypeInvalidException();
                }
            }
            case "salario" -> {
                double salario = converterSalario(valor);
                emp.setSalario(salario);
            }
            case "comissao" -> {
                if (emp.getTipo().equals("comissionado")){
                    double comissao = converterComissao(valor);
                    assert emp instanceof Comissionado;
                    ((Comissionado) emp).setComissao(comissao);
                }
                else throw new EmployeeNotCommissionedException();
            }
            case "metodoPagamento" -> {
                if (valor.equals("emMaos") || valor.equals("banco") || valor.equals("correios")) emp.setMetodoPagamento(valor);
                else throw new PaymentMethodInvalidException();
            }
            case "banco" -> {
                if (valor != null) emp.setBanco(valor);
                else throw new BankNullException();
            }
            case "agencia" -> {
                if (valor != null) emp.setAgencia(valor);
                else throw new BankNullException();
            }
            case "contaCorrente" -> {
                if (valor != null) emp.setContaCorrente(valor);
                else throw new AccountNullException();
            }
            case "sindicalizado" -> {
                if (valor.equals("false") || valor.equals("true")) emp.setSindicalizado(Boolean.parseBoolean(valor));
                else throw new ValueNotBooleanException();
            }
            case "idSindicato" -> {
                if (valor != null){
                    if (Sindicato.inSindicato(valor)) throw new IdSyndicateInvalidException();
                    else{
                        Sindicato.removeIdSindicato(emp.getIdSindicato());
                        emp.setIdSindicato(valor);
                        Sindicato.addIdSindicato(valor);
                    }
                }
                else throw new IdSyndicateNullException();
            }
            case "taxaSindical" -> {
                double taxaSindical = converterTaxaSindical(valor);
                emp.setTaxaSindical(taxaSindical);
            }
            case "agendaPagamento" -> {
                if(valor != null) {
                    if(agendasPagamento.contains(valor)) emp.setAgendaPagamento(valor);
                    else throw new SchedulePaymentUnavailableException();
                }
                else throw new SchedulePaymentUnavailableException();
            }
            default -> throw new AttributeNotFoundException();
        }
        Empregado empregado = empregados.get(id);

        Command c = new Command() {
            @Override
            public void executar() {
                empregados.put(id, empregado);
            }
            @Override
            public void desfazer() {
                empregados.put(id, backup);
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();
    }

    public void alteraEmpregado(String id, String atributo, String valor, String comissao) throws  Exception {
        if (id.isEmpty()) throw new EmployeeNullException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmployeeNotFoundException();
        Empregado backup;
        if (emp instanceof Horista) backup = new Horista((Horista) emp);
        else if (emp instanceof Comissionado) backup = new Comissionado((Comissionado) emp);
        else backup = new Assalariado((Assalariado) emp);
        if (!atributo.equals("tipo") || !(valor.equals("horista") || valor.equals("comissionado"))) throw new AttributeNotFoundException();


        if (valor.equals("horista")){
            double salarioDobule = converterSalario(comissao);
            Horista horista = converterHorista(id, emp);
            horista.setSalario(salarioDobule);
            empregados.put(id, horista);
        }
        else{
            double comissaoDouble = converterComissao(comissao);
            Comissionado comissionado = converterComissionado(id, comissaoDouble, emp);
            empregados.put(id, comissionado);
        }

        Empregado empregado = empregados.get(id);
        Command c = new Command() {
            @Override
            public void executar() {
                empregados.put(id, empregado);
            }
            @Override
            public void desfazer() {
                empregados.put(id, backup);
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();
    }

    public void alteraEmpregado(String id, String atributo, String valor, String idSindicato, String taxaSindical) throws  Exception {
        if (id.isEmpty()) throw new EmployeeNullException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmployeeNotFoundException();
        Empregado backup;
        if (emp instanceof Horista) backup = new Horista((Horista) emp);
        else if (emp instanceof Comissionado) backup = new Comissionado((Comissionado) emp);
        else backup = new Assalariado((Assalariado) emp);
        if (atributo.equals("sindicalizado") && valor.equals("true")){
            if (idSindicato.isEmpty()) throw new IdSyndicateNullException();
            double taxaSindicalDouble = converterTaxaSindical(taxaSindical);
            if (Sindicato.inSindicato(idSindicato)) throw new IdSyndicateInvalidException();
            emp.setSindicalizado(true);
            emp.setIdSindicato(idSindicato);
            emp.setTaxaSindical(taxaSindicalDouble);
        }
        else throw new AttributeNotFoundException();

        Command c = new Command() {
            @Override
            public void executar() {
                empregados.put(id, emp);
                Sindicato.addIdSindicato(idSindicato);
            }

            @Override
            public void desfazer() {
                empregados.put(id, backup);
                Sindicato.removeIdSindicato(idSindicato);
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();
    }

    public void alteraEmpregado(String id, String atributo, String valor, String banco, String agencia, String contaCorrente) throws  Exception {
        if (id.isEmpty()) throw new EmployeeNullException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmployeeNotFoundException();
        Empregado backup;
        if (emp instanceof Horista) backup = new Horista((Horista) emp);
        else if (emp instanceof Comissionado) backup = new Comissionado((Comissionado) emp);
        else backup = new Assalariado((Assalariado) emp);
        if (atributo.equals("metodoPagamento") && valor.equals("banco")){
            if (banco.isEmpty()) throw new BankNullException();
            if (agencia.isEmpty()) throw new AgencyNullException();
            if (contaCorrente.isEmpty()) throw new AccountNullException();
            emp.setMetodoPagamento("banco");
            emp.setBanco(banco);
            emp.setAgencia(agencia);
            emp.setContaCorrente(contaCorrente);
        }
        else throw new AttributeNotFoundException();

        Command c = new Command() {
            @Override
            public void executar() {
                empregados.put(id, emp);
            }

            @Override
            public void desfazer() {
                empregados.put(id, backup);
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();
    }

    public void lancaCartao(String id, String data, String horas) throws Exception{
        if (id.isEmpty()) throw new EmployeeNullException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmployeeNotFoundException();
        if(!(emp instanceof Horista)) throw new EmployeeNotHourlyException();
        LocalDate dataT = converterData(data);
        double horasT = receberHora(horas);
        Cartao cartao = new Cartao(dataT, horasT);

        Command c = new Command() {
            @Override
            public void executar() {
                ((Horista) emp).lancaCartao(cartao);
            }
            @Override
            public void desfazer() {
                ((Horista) emp).removerCartao(cartao);
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();
    }

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        if (id.isEmpty()) throw new EmployeeNullException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmployeeNotFoundException();
        if(!(emp instanceof Horista)) throw new EmployeeNotHourlyException();
        LocalDate dataI = converterDataInicial(dataInicial);
        LocalDate dataF = converterDataFinal(dataFinal);
        if(dataI.isAfter(dataF)) throw new DateLastPriorFirstException();
        double horasNormais = 0;
        for(Cartao cartao : ((Horista) emp).getCartoesDePonto()) if(((cartao.getDataAsLocalDate().isAfter(dataI) || cartao.getDataAsLocalDate().isEqual(dataI)) && cartao.getDataAsLocalDate().isBefore(dataF)) && cartao.getHorasTrabalhadas() >= 8) horasNormais += 8;
        return converterHora(horasNormais);
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        if (id.isEmpty()) throw new EmployeeNullException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmployeeNotFoundException();
        if(!(emp instanceof Horista)) throw new EmployeeNotHourlyException();
        LocalDate dataI = converterDataInicial(dataInicial);
        LocalDate dataF = converterDataFinal(dataFinal);
        if(dataI.isAfter(dataF)) throw new DateLastPriorFirstException();
        double horasExtras = 0;
        for(Cartao cartao : ((Horista) emp).getCartoesDePonto()) if(((cartao.getDataAsLocalDate().isAfter(dataI) || cartao.getDataAsLocalDate().isEqual(dataI)) && cartao.getDataAsLocalDate().isBefore(dataF)) && cartao.getHorasTrabalhadas() > 8) horasExtras += cartao.getHorasTrabalhadas() - 8;
        return converterHora(horasExtras);
    }

    public void lancaVenda(String id, String data, String valor) throws Exception {
        if (id.isEmpty()) throw new EmployeeNullException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmployeeNotFoundException();
        if(!(emp instanceof Comissionado)) throw new EmployeeNotCommissionedException();
        LocalDate dataT = converterData(data);
        double valorT = receberValor(valor);
        Venda venda = new Venda(dataT, valorT);

        Command c = new Command() {
            @Override
            public void executar() {
                ((Comissionado) emp).lancaVenda(venda);
            }
            @Override
            public void desfazer() {
                ((Comissionado) emp).removerVenda(venda);
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();
    }

    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws Exception {
        if (id.isEmpty()) throw new EmployeeNullException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmployeeNotFoundException();
        if(!(emp instanceof Comissionado)) throw new EmployeeNotCommissionedException();
        LocalDate dataI = converterDataInicial(dataInicial);
        LocalDate dataF = converterDataFinal(dataFinal);
        if(dataI.isAfter(dataF)) throw new DateLastPriorFirstException();
        double valorTotal = 0;
        for(Venda venda : ((Comissionado) emp).getVendas()) if(((venda.getDataAsLocalDate().isAfter(dataI) || venda.getDataAsLocalDate().isEqual(dataI)) && venda.getDataAsLocalDate().isBefore(dataF))) valorTotal += venda.getValor();
        return String.format("%.2f", valorTotal);
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception{
        if (membro.isEmpty()) throw new MemberNullException();
        Empregado emp = null;
        for (Empregado empregado : empregados.values()) if (empregado.getIdSindicato() != null && empregado.getIdSindicato().equals(membro)) { emp = empregado; break; }
        if (emp == null) throw new MemberNotFoundException();
        if(!emp.isSindicalizado()) throw new EmployeeNotInSyndicateException();
        LocalDate dataT = converterData(data);
        double valorT = receberValor(valor);
        TaxaServico taxaServico = new TaxaServico(dataT, valorT);

        Empregado finalEmp = emp;
        Command c = new Command() {
            @Override
            public void executar() {
                finalEmp.lancaTaxaServico(taxaServico);
            }
            @Override
            public void desfazer() {
                finalEmp.removerTaxaServico(taxaServico);
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();
    }

    public String getTaxasServico(String membro, String dataInicial, String dataFinal) throws Exception {
        if (membro.isEmpty()) throw new MemberNullException();
        Empregado emp = empregados.get(membro);
        if (emp == null) throw new MemberNotFoundException();
        if(!emp.isSindicalizado()) throw new EmployeeNotInSyndicateException();
        LocalDate dataI = converterDataInicial(dataInicial);
        LocalDate dataF = converterDataFinal(dataFinal);
        if(dataI.isAfter(dataF)) throw new DateLastPriorFirstException();
        double valorTotal = 0;
        for(TaxaServico taxaServico : emp.getTaxasServicos()) if(((taxaServico.getDataAsLocalDate().isAfter(dataI) || taxaServico.getDataAsLocalDate().isEqual(dataI)) && taxaServico.getDataAsLocalDate().isBefore(dataF))) valorTotal += taxaServico.getValor();
        return String.format("%.2f", valorTotal);
    }
    private boolean isAntigo(HashMap<String, Empregado> empregados) {
        if (agendasPagamento.size() > 3) {
            return false;
        }

        for (Empregado empregado : empregados.values()) {
            String agendaAtual = empregado.getAgendaPagamento();

            if (empregado instanceof Horista && !agendaAtual.equals("semanal 5")) {
                return false;
            } else if (empregado instanceof Assalariado && !agendaAtual.equals("mensal $")) {
                return false;
            } else if (empregado instanceof Comissionado && !agendaAtual.equals("semanal 2 5")) {
                return false;
            }
        }

        return true;
    }


    private boolean diaPagamento(Empregado empregado, LocalDate dataFolha) {
        String[] agenda = empregado.getAgendaPagamento().split(" ");
        String tipoAgenda = agenda[0];
        if (tipoAgenda.equals("mensal")) {
            if (agenda[1].equals("$")) return dataFolha.isEqual(dataFolha.with(TemporalAdjusters.lastDayOfMonth()));
            else {
                int diaDoMes = Integer.parseInt(agenda[1]);
                return dataFolha.getDayOfMonth() == diaDoMes;
            }
        }
        else if (tipoAgenda.equals("semanal")) {
            int diaDaSemanaAlvo = Integer.parseInt(agenda[agenda.length - 1]);
            int diaDaSemanaAtual = dataFolha.getDayOfWeek().getValue();
            if (diaDaSemanaAtual != diaDaSemanaAlvo) return false;
            if (agenda.length == 2) return true;
            else {
                int semanas = Integer.parseInt(agenda[1]);
                LocalDate primeiroDiaDoAno = dataFolha.with(TemporalAdjusters.firstDayOfYear());
                LocalDate primeiroDiaDaSemanaAlvo = primeiroDiaDoAno.with(TemporalAdjusters.nextOrSame(dataFolha.getDayOfWeek()));
                long diasDeDiferenca = ChronoUnit.DAYS.between(primeiroDiaDaSemanaAlvo, dataFolha);
                long numeroDaOcorrencia = (diasDeDiferenca / 7) + 1;
                return numeroDaOcorrencia % semanas == 0;
            }
        }
        return false;
    }


    private static Horista converterHorista(String id, Empregado emp) throws EmployeeNotFoundException {
        Horista empregado = new Horista(id, emp.getNome(), emp.getEndereco(), "horista", emp.getSalario());
        empregado.setMetodoPagamento(emp.getMetodoPagamento());
        empregado.setBanco(emp.getBanco());
        empregado.setAgencia(emp.getAgencia());
        empregado.setContaCorrente(emp.getContaCorrente());
        empregado.setSindicalizado(emp.isSindicalizado());
        empregado.setIdSindicato(emp.getIdSindicato());
        empregado.setTaxaSindical(emp.getTaxaSindical());
        return empregado;
    }

    private static Comissionado converterComissionado(String id, double comissao, Empregado emp) throws EmployeeNotFoundException {
        Comissionado empregado = new Comissionado(id, emp.getNome(), emp.getEndereco(), "comissionado", emp.getSalario(), comissao);
        empregado.setMetodoPagamento(emp.getMetodoPagamento());
        empregado.setBanco(emp.getBanco());
        empregado.setAgencia(emp.getAgencia());
        empregado.setContaCorrente(emp.getContaCorrente());
        empregado.setSindicalizado(emp.isSindicalizado());
        empregado.setIdSindicato(emp.getIdSindicato());
        empregado.setTaxaSindical(emp.getTaxaSindical());
        return empregado;
    }

    private static Assalariado converterAssalariado(String id, Empregado emp) throws EmployeeNotFoundException {
        Assalariado empregado = new Assalariado(id, emp.getNome(), emp.getEndereco(), "assalariado", emp.getSalario());
        empregado.setMetodoPagamento(emp.getMetodoPagamento());
        empregado.setBanco(emp.getBanco());
        empregado.setAgencia(emp.getAgencia());
        empregado.setContaCorrente(emp.getContaCorrente());
        empregado.setSindicalizado(emp.isSindicalizado());
        empregado.setIdSindicato(emp.getIdSindicato());
        empregado.setTaxaSindical(emp.getTaxaSindical());
        return empregado;
    }

    /*public String totalFolha(String data) throws Exception {
        HashMap<String, Empregado> empregados = getEmpregados();
        LocalDate dataFolha = converterData(data);
        double totalFolha = 0;

        for (Empregado empregado : empregados.values()) {
            if (empregado instanceof Horista horista && dataFolha.getDayOfWeek() == DayOfWeek.FRIDAY) {
                LocalDate dataInicial = dataFolha.minusDays(6);
                double[] horas = horista.getHorasNoPeriodo(dataInicial, dataFolha);
                totalFolha += horas[0] * empregado.getSalario() + horas[1] * empregado.getSalario() * 1.5;
            }
            else if (empregado instanceof Assalariado && isUltimoDiaUtil(dataFolha)) totalFolha += empregado.getSalario();
            else if (empregado instanceof Comissionado comissionado && isDiaDePagamentoComissionado(dataFolha)) {
                LocalDate dataInicial = dataFolha.minusDays(13);
                double totalVendas = comissionado.getVendasNoPeriodo(dataInicial, dataFolha);
                BigDecimal salarioBD = BigDecimal.valueOf(empregado.getSalario());
                BigDecimal salarioFixoBD = salarioBD.multiply(new BigDecimal("12")).divide(new BigDecimal("26"), 2, RoundingMode.DOWN);
                BigDecimal totalVendasBD = BigDecimal.valueOf(totalVendas);
                BigDecimal comissaoTaxaBD = BigDecimal.valueOf(comissionado.getComissao());
                BigDecimal comissaoBD = totalVendasBD.multiply(comissaoTaxaBD).setScale(2, RoundingMode.DOWN);
                totalFolha += salarioFixoBD.add(comissaoBD).doubleValue();
            }
        }
        return String.format("%.2f", totalFolha).replace('.', ',');
    }*/

    public void rodaFolha(String data, String saida) throws Exception {
        LocalDate dataFolha = converterData(data);
        List<Empregado> empregadosOrdenados = new ArrayList<>(getEmpregados().values());
        empregadosOrdenados.sort(Comparator.comparing(Empregado::getNome));
        HashMap<String, List<Cartao>> cartoesPagos = new HashMap<>();
        HashMap<String, List<Venda>> vendasPagas = new HashMap<>();
        StringBuilder relatorio = new StringBuilder();
        double totalFolhaGeral = 0;

        StringBuilder folhaHoristas = new StringBuilder();
        folhaHoristas.append("===============================================================================================================================\n");
        folhaHoristas.append("===================== HORISTAS ================================================================================================\n");
        folhaHoristas.append("===============================================================================================================================\n");
        folhaHoristas.append("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n");
        folhaHoristas.append("==================================== ===== ===== ============= ========= =============== ======================================\n");
        double totalBrutoHoristas = 0, totalDescontosHoristas = 0, totalLiquidoHoristas = 0;
        int totalHorasNormais = 0, totalHorasExtras = 0;

        StringBuilder folhaAssalariados = new StringBuilder();
        folhaAssalariados.append("===============================================================================================================================\n");
        folhaAssalariados.append("===================== ASSALARIADOS ============================================================================================\n");
        folhaAssalariados.append("===============================================================================================================================\n");
        folhaAssalariados.append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n");
        folhaAssalariados.append("================================================ ============= ========= =============== ======================================\n");
        double totalBrutoAssalariados = 0, totalDescontosAssalariados = 0, totalLiquidoAssalariados = 0;

        StringBuilder folhaComissionados = new StringBuilder();
        folhaComissionados.append("===============================================================================================================================\n");
        folhaComissionados.append("===================== COMISSIONADOS ===========================================================================================\n");
        folhaComissionados.append("===============================================================================================================================\n");
        folhaComissionados.append("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\n");
        folhaComissionados.append("===================== ======== ======== ======== ============= ========= =============== ======================================\n");
        double totalFixoComissionados = 0, totalVendasComissionados = 0, totalComissaoComissionados = 0, totalBrutoComissionados = 0, totalDescontosComissionados = 0, totalLiquidoComissionados = 0;

        if (dataFolha.getDayOfWeek() == DayOfWeek.FRIDAY) {
            for (Empregado empregado : empregadosOrdenados) {
                if (empregado instanceof Horista horista) {
                    if (horista.isSindicalizado()) horista.setDebtSyndicate(horista.getDebtSyndicate() + horista.getTaxaSindical() * 7);
                    LocalDate dataInicial = dataFolha.minusDays(6);
                    double[] horas = horista.getHorasNoPeriodo(dataInicial, dataFolha);
                    double salarioBruto = horas[0] * horista.getSalario() + horas[1] * horista.getSalario() * 1.5;
                    double descontos = 0, salarioLiquido = 0;
                    if (salarioBruto > 0) {
                        descontos = horista.getTaxasServicoNoPeriodo(dataInicial, dataFolha) + horista.getDebtSyndicate();
                        salarioLiquido = salarioBruto - descontos;
                        if (salarioLiquido < 0) {
                            horista.setDebtSyndicate(descontos - salarioBruto);
                            descontos = salarioBruto;
                            salarioLiquido = 0;
                        }
                        else horista.setDebtSyndicate(0);
                    }
                    String metodoPagamentoStr;
                    switch (horista.getMetodoPagamento()) {
                        case "emMaos" -> metodoPagamentoStr = "Em maos";
                        case "correios" -> metodoPagamentoStr = String.format("Correios, %s", horista.getEndereco());
                        default -> metodoPagamentoStr = String.format("%s, Ag. %s CC %s", horista.getBanco(), horista.getAgencia(), horista.getContaCorrente());
                    }
                    folhaHoristas.append(String.format("%-36s %5.0f %5.0f %13.2f %9.2f %15.2f %s\n", horista.getNome(), horas[0], horas[1], salarioBruto, descontos, salarioLiquido, metodoPagamentoStr));
                    totalHorasNormais += (int) horas[0];
                    totalHorasExtras += (int) horas[1];
                    totalBrutoHoristas += salarioBruto;
                    totalDescontosHoristas += descontos;
                    totalLiquidoHoristas += salarioLiquido;
                    totalFolhaGeral += salarioBruto;
                    List<Cartao> cartoesDoPeriodo = new ArrayList<>();
                    for(Cartao c : horista.getCartoesDePonto()) if(!c.getDataAsLocalDate().isBefore(dataInicial) && c.getDataAsLocalDate().isBefore(dataFolha.plusDays(1))) cartoesDoPeriodo.add(c);
                    if (!cartoesDoPeriodo.isEmpty()) cartoesPagos.put(horista.getId(), cartoesDoPeriodo);
                }
            }
        }

        if (isUltimoDiaUtil(dataFolha)) {
            for (Empregado empregado : empregadosOrdenados) {
                if (empregado instanceof Assalariado) {
                    LocalDate dataInicial = dataFolha.with(TemporalAdjusters.firstDayOfMonth());
                    double salarioBruto = empregado.getSalario();
                    double descontos = empregado.getTaxasServicoNoPeriodo(dataInicial, dataFolha);
                    if (empregado.isSindicalizado()) descontos += empregado.getTaxaSindical() * dataFolha.lengthOfMonth();
                    double salarioLiquido = salarioBruto - descontos;
                    String metodoPagamentoStr;
                    switch (empregado.getMetodoPagamento()) {
                        case "emMaos" -> metodoPagamentoStr = "Em maos";
                        case "correios" -> metodoPagamentoStr = String.format("Correios, %s", empregado.getEndereco());
                        default -> metodoPagamentoStr = String.format("%s, Ag. %s CC %s", empregado.getBanco(), empregado.getAgencia(), empregado.getContaCorrente());
                    }
                    folhaAssalariados.append(String.format("%-48s %13.2f %9.2f %15.2f %s\n", empregado.getNome(), salarioBruto, descontos, salarioLiquido, metodoPagamentoStr));
                    totalBrutoAssalariados += salarioBruto;
                    totalDescontosAssalariados += descontos;
                    totalLiquidoAssalariados += salarioLiquido;
                    totalFolhaGeral += salarioBruto;
                }
            }
        }

        if (isDiaDePagamentoComissionado(dataFolha)) {
            for (Empregado empregado : empregadosOrdenados) {
                if (empregado instanceof Comissionado comissionado) {
                    LocalDate dataInicial = dataFolha.minusDays(13);
                    BigDecimal salarioBD = BigDecimal.valueOf(comissionado.getSalario());
                    BigDecimal salarioFixoBD = salarioBD.multiply(new BigDecimal("12")).divide(new BigDecimal("26"), 2, RoundingMode.DOWN);
                    double vendas = comissionado.getVendasNoPeriodo(dataInicial, dataFolha);
                    BigDecimal totalVendasBD = BigDecimal.valueOf(vendas);
                    BigDecimal comissaoTaxaBD = BigDecimal.valueOf(comissionado.getComissao());
                    BigDecimal comissaoBD = totalVendasBD.multiply(comissaoTaxaBD).setScale(2, RoundingMode.DOWN);
                    double salarioFixo = salarioFixoBD.doubleValue();
                    double comissao = comissaoBD.doubleValue();
                    double salarioBruto = salarioFixo + comissao;
                    double descontos = comissionado.getTaxasServicoNoPeriodo(dataInicial, dataFolha);
                    if (comissionado.isSindicalizado()) descontos += comissionado.getTaxaSindical() * 14;
                    double salarioLiquido = salarioBruto - descontos;
                    String metodoPagamentoStr;
                    switch (comissionado.getMetodoPagamento()) {
                        case "emMaos" -> metodoPagamentoStr = "Em maos";
                        case "correios" -> metodoPagamentoStr = String.format("Correios, %s", comissionado.getEndereco());
                        default -> metodoPagamentoStr = String.format("%s, Ag. %s CC %s", comissionado.getBanco(), comissionado.getAgencia(), comissionado.getContaCorrente());
                    }
                    folhaComissionados.append(String.format("%-21s %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f %s\n", comissionado.getNome(), salarioFixo, vendas, comissao, salarioBruto, descontos, salarioLiquido, metodoPagamentoStr));
                    totalFixoComissionados += salarioFixo;
                    totalVendasComissionados += vendas;
                    totalComissaoComissionados += comissao;
                    totalBrutoComissionados += salarioBruto;
                    totalDescontosComissionados += descontos;
                    totalLiquidoComissionados += salarioLiquido;
                    totalFolhaGeral += salarioBruto;
                    List<Venda> vendasDoPeriodo = new ArrayList<>();
                    for(Venda v : comissionado.getVendas()) if(!v.getDataAsLocalDate().isBefore(dataInicial) && v.getDataAsLocalDate().isBefore(dataFolha.plusDays(1))) vendasDoPeriodo.add(v);
                    if (!vendasDoPeriodo.isEmpty()) vendasPagas.put(comissionado.getId(), vendasDoPeriodo);
                }
            }
        }

        relatorio.append("FOLHA DE PAGAMENTO DO DIA ").append(dataFolha).append("\n");
        relatorio.append("====================================\n\n");
        relatorio.append(folhaHoristas);
        relatorio.append("\n");
        relatorio.append(String.format("TOTAL HORISTAS %27d %5d %13.2f %9.2f %15.2f\n\n", totalHorasNormais, totalHorasExtras, totalBrutoHoristas, totalDescontosHoristas, totalLiquidoHoristas));
        relatorio.append(folhaAssalariados);
        relatorio.append("\n");
        relatorio.append(String.format("TOTAL ASSALARIADOS %43.2f %9.2f %15.2f\n\n", totalBrutoAssalariados, totalDescontosAssalariados, totalLiquidoAssalariados));
        relatorio.append(folhaComissionados);
        relatorio.append("\n");
        relatorio.append(String.format("TOTAL COMISSIONADOS %10.2f %8.2f %8.2f %13.2f %9.2f %15.2f\n\n", totalFixoComissionados, totalVendasComissionados, totalComissaoComissionados, totalBrutoComissionados, totalDescontosComissionados, totalLiquidoComissionados));
        relatorio.append(String.format("TOTAL FOLHA: %.2f\n", totalFolhaGeral));

        String relatorioFinal = relatorio.toString();
        Command c = new Command() {
            @Override
            public void executar() {
                try {
                    FileWriter writer = new FileWriter(saida);
                    writer.write(relatorioFinal);
                    writer.close();
                }
                catch (Exception e) {
                    throw new RuntimeException("Falha ao escrever arquivo de folha.", e);
                }
                for (String empId : cartoesPagos.keySet()) {
                    Horista h = (Horista) getEmpregados().get(empId);
                    if (h != null) h.getCartoesDePonto().removeAll(cartoesPagos.get(empId));
                }
                for (String empId : vendasPagas.keySet()) {
                    Comissionado co = (Comissionado) getEmpregados().get(empId);
                    if (co != null) co.getVendas().removeAll(vendasPagas.get(empId));
                }
            }
            @Override
            public void desfazer() {
                File arquivo = new File(saida);
                if (arquivo.exists()) arquivo.delete();
                for (String empId : cartoesPagos.keySet()) {
                    Horista h = (Horista) getEmpregados().get(empId);
                    if (h != null) h.getCartoesDePonto().addAll(cartoesPagos.get(empId));
                }
                for (String empId : vendasPagas.keySet()) {
                    Comissionado co = (Comissionado) getEmpregados().get(empId);
                    if (co != null) co.getVendas().addAll(vendasPagas.get(empId));
                }
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();
    }

    private static double converterSalario(String numero) throws Exception {
        if (numero.isEmpty()) throw new SalaryNullException();
        try {
            double resultado = Double.parseDouble(numero.replace(",", "."));
            if (resultado < 0) throw new SalaryNotPositiveException();
            return resultado;
        }
        catch(NumberFormatException e) {
            throw new SalaryNotNumberException();
        }
    }
    private static double converterComissao(String numero) throws Exception {
        if (numero.isEmpty()) throw new CommissionNullException();
        try {
            double resultado = Double.parseDouble(numero.replace(",", "."));
            if (resultado < 0) throw new CommissionNotPositiveException();
            return resultado;
        }
        catch(NumberFormatException e) {
            throw new CommissionNotNumberException();
        }
    }

    private static double converterTaxaSindical(String numero) throws Exception {
        if (numero.isEmpty()) throw new SyndicateFeeNullException();
        try {
            double resultado = Double.parseDouble(numero.replace(",", "."));
            if (resultado < 0) throw new SyndicateFeeNotPositiveException();
            return resultado;
        }
        catch(NumberFormatException e) {
            throw new SyndicateFeeNotNumberException();
        }
    }

    private static double receberHora(String numero) throws Exception {
        if (numero.isEmpty()) throw new HoursNullException();
        try {
            double resultado = Double.parseDouble(numero.replace(",", "."));
            if (resultado <= 0) throw new HoursNotPositiveException();
            return resultado;
        }
        catch(NumberFormatException e) {
            throw new HoursNotNumberException();
        }
    }

    private static double receberValor(String numero) throws Exception {
        if (numero.isEmpty()) throw new ValueNullException();
        try {
            double resultado = Double.parseDouble(numero.replace(",", "."));
            if (resultado <= 0) throw new ValueNotPositiveException();
            return resultado;
        }
        catch(NumberFormatException e) {
            throw new ValueNotNumberException();
        }
    }

    private static LocalDate converterData(String data) throws Exception {
        if (data.isEmpty()) throw new DateInvalidException();
        try {
            if (data.contains("30/2") || data.contains("30/02")) throw new DateInvalidException();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            throw new DateInvalidException();
        }
    }

    private static LocalDate converterDataInicial(String data) throws Exception {
        if (data.isEmpty()) throw new DateInvalidException();
        try {
            if (data.contains("30/2") || data.contains("30/02")) throw new DateInvalidFirstException();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            throw new DateInvalidFirstException();
        }
    }

    private static LocalDate converterDataFinal(String data) throws Exception {
        if (data.isEmpty()) throw new DateInvalidException();
        try {
            if (data.contains("30/2/") || data.contains("30/02/")) throw new DateInvalidLastException();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            throw new DateInvalidLastException();
        }
    }

    private static String converterHora(double numero) {
        DecimalFormat hora = new DecimalFormat();
        hora.applyPattern("#.##");
        return (hora.format(numero).replace(".", ","));
    }

    private static String darId(int i){
        return String.format("id%d", i);
    }

    private static boolean isUltimoDiaUtil(LocalDate data) {
        LocalDate ultimoDiaDoMes = data.with(TemporalAdjusters.lastDayOfMonth());
        while (ultimoDiaDoMes.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDiaDoMes.getDayOfWeek() == DayOfWeek.SUNDAY) ultimoDiaDoMes = ultimoDiaDoMes.minusDays(1);
        return data.isEqual(ultimoDiaDoMes);
    }

    private static boolean isDiaDePagamentoComissionado(LocalDate data) {
        if (data.getDayOfWeek() != DayOfWeek.FRIDAY) return false;
        LocalDate primeiroPagamento = LocalDate.of(2005, 1, 14);
        if (data.isBefore(primeiroPagamento)) return false;
        long diasDesdePrimeiroPagamento = java.time.temporal.ChronoUnit.DAYS.between(primeiroPagamento, data);
        return diasDesdePrimeiroPagamento % 14 == 0;
    }


}