package org.openpdfsign;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Strings;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class CLIApplication {

    public static void main(String[] args) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, OperatorCreationException, PKCSException {
        CommandLineArguments cla = new CommandLineArguments();
        JCommander parser = JCommander.newBuilder()
                .addObject(cla)
                .build();


        try {
            parser.parse(args);
        }
        catch(ParameterException ex) {
            parser.usage();
            return;
        }

        System.out.println("Running with " + cla);

        //convert to keystore, if not already given
        byte[] keystore = null;
        char[] keystorePassphrase = null;
        if (!Strings.isStringEmpty(cla.getCertificateFile()) &&
            !Strings.isStringEmpty(cla.getKeyFile())) {

            keystore = KeyStoreLoader.loadKeyStoreFromKeys(
                    Paths.get(cla.getCertificateFile()),
                    Paths.get(cla.getKeyFile()),
                    cla.getKeyPassphrase().toCharArray(),
                    cla.getKeyPassphrase().toCharArray()
            );
            keystorePassphrase = cla.getKeyPassphrase().toCharArray();
        }
        else if (!Strings.isStringEmpty(cla.getKeyFile()) &&
                !Strings.isStringEmpty(cla.getKeyPassphrase())) {
            keystore = Files.readAllBytes(Paths.get(cla.getKeyFile()));
            keystorePassphrase = cla.getKeyPassphrase().toCharArray();
        }

        Path pdfFile = Paths.get(cla.getInputFile());
        Path outputFile = Paths.get(cla.getOutputFile());

        Signer s = new Signer();
        s.signPdf(pdfFile, outputFile, keystore, keystorePassphrase, cla);
    }
}
