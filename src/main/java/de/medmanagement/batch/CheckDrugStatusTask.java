package de.medmanagement.batch;

import de.medmanagement.mail.EmailServiceImpl;
import de.medmanagement.model.Drug;
import de.medmanagement.model.UserDataService;
import de.medmanagement.rights.User;
import de.medmanagement.rights.UsersRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CheckDrugStatusTask implements Tasklet {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private EmailServiceImpl emailService;

    @Value( "${perform.job.check_drug_status}" )
    private String performJob;

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception
    {
        if ( performJob.equals("true") ) {
            // System.out.println("--> Task CheckDrugStatus execute starts");
            ArrayList<Drug> scarceDrugs = new ArrayList<Drug>();
            List<User> users = usersRepository.findAll();
            for (User user : users) {
                if (!(user.isAccountExpired() ||
                        user.isAccountLocked() ||
                        user.isCredentialsExpired() ||
                        user.isDisabled())) {
                    ArrayList<Drug> drugs = userDataService.getDrugs(user.getName());
                    for (Drug drug : drugs) {
                        if (drug.getDaysLeft() < 14) {
                            scarceDrugs.add(drug);
                        }
                    }
                    if (!scarceDrugs.isEmpty()) {
                        informUser(user, scarceDrugs);
                    }
                }
                scarceDrugs.clear();
            }
            // System.out.println("<-- Task CheckDrugStatus execute done");
        }
        return RepeatStatus.FINISHED;
    }

    private void informUser(User user, ArrayList<Drug> scarceDrugs) {
        StringBuffer sb = new StringBuffer();
        sb.append("Hallo Nutzer " + user.getFirstname() + " " + user.getLastname() + ",\n\n");
        sb.append("die folgenden Medikamente neigen sich dem Ende zu:\n\n");
        for (Drug drug: scarceDrugs) {
            sb.append(drug.getName() + ": " + drug.getDaysLeft() + "\n");
        }
        emailService.sendSimpleMessage(user.getEmail(), "MedManagement Info", sb.toString());
    }
}