// ContaBancaria.java
public abstract class ContaBancaria {
    protected double saldo;

    public ContaBancaria(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    public void depositar(double valor) {
        saldo += valor;
    }

    public abstract void sacar(double valor) throws SaldoInsuficienteException;

    public double getSaldo() {
        return saldo;
    }
}

// ContaCorrente.java
public class ContaCorrente extends ContaBancaria {
    public ContaCorrente(double saldoInicial) {
        super(saldoInicial);
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque de " + valor);
        }
        saldo -= valor;
    }
}

// ContaPoupanca.java
public class ContaPoupanca extends ContaBancaria {
    public ContaPoupanca(double saldoInicial) {
        super(saldoInicial);
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque de " + valor);
        }
        saldo -= valor;
    }
}

// SaldoInsuficienteException.java
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}

// VerificacaoSaldoAspect.aj
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class VerificacaoSaldoAspect {

    @Pointcut("execution(* ContaBancaria.sacar(..)) && args(valor)")
    public void verificacaoSaldo(double valor) {}

    @Before("verificacaoSaldo(valor)")
    public void checarSaldo(JoinPoint joinPoint, double valor) throws Throwable {
        ContaBancaria conta = (ContaBancaria) joinPoint.getTarget();
        if (conta.getSaldo() < valor) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque de " + valor);
        }
    }

    @AfterThrowing(pointcut = "verificacaoSaldo(valor)", throwing = "ex")
    public void logSaldoInsuficiente(double valor, Exception ex) {
        System.out.println("Erro ao tentar sacar " + valor + ": " + ex.getMessage());
    }
}

// Banco.java
public class Banco {
    public static void main(String[] args) {
        ContaBancaria contaCorrente = new ContaCorrente(1000);
        ContaBancaria contaPoupanca = new ContaPoupanca(500);

        try {
            contaCorrente.sacar(200);
            System.out.println("Saque realizado com sucesso. Saldo atual: " + contaCorrente.getSaldo());
        } catch (SaldoInsuficienteException e) {
            System.out.println(e.getMessage());
        }

        try {
            contaPoupanca.sacar(600);
            System.out.println("Saque realizado com sucesso. Saldo atual: " + contaPoupanca.getSaldo());
        } catch (SaldoInsuficienteException e) {
            System.out.println(e.getMessage());
        }
    }
}
