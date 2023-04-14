package lithium.service.mock.gamstop.service;

import lithium.service.mock.gamstop.model.Person;
import lithium.service.mock.gamstop.model.Persons;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class BlockedPersonService {

    private Persons persons;

    public BlockedPersonService() {
        this.persons = new Persons();
        this.persons.add(defaultPerson());
    }
    public Persons addBlockedPersons(Persons persons) {
        log.info("{}", persons);
        this.persons.addAll(persons);
        return this.persons;
    }

    public boolean isBlocked(String firstName, String lastName, String dateOfBirth, String email, String postcode, String mobile) throws ParseException {
        log.info("{}, {}, {},{}, {}, {}", firstName, lastName, dateOfBirth, email, postcode, mobile);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dob = new Date();
        if (!StringUtil.isEmpty(dateOfBirth)) dob = dateFormat.parse(dateOfBirth);
        LocalDate dateThreeTenFormat = Instant.ofEpochMilli(dob.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        for (Person p: persons) {
            if (email.startsWith("gamstop")) return true;
            if(p.getFirstName().equalsIgnoreCase(firstName) &&
                    p.getLastName().equalsIgnoreCase(lastName) &&
                    p.getEmail().equalsIgnoreCase(email) &&
                    p.getPostcode().equalsIgnoreCase(postcode) &&
                    p.getMobile().equalsIgnoreCase(mobile) &&
                    p.getDateOfBirth().compareTo(dateThreeTenFormat)==0){
                return true;
            }
        }
        return false;
    }

    public boolean isBlocked(Person person) throws ParseException {
        for (Person p: this.persons) {
            log.info("------->Person------{}", person);
            log.info("------->P-----------{}", p);
            if (person.getEmail().startsWith("gamstop")) {
                return true;
            }
            if (p.equals(person)){
                return true;
            }
        }
        return false;
    }

    public boolean hasSEhistory(String firstName, String lastName, String dateOfBirth, String email, String postcode, String mobile) throws ParseException {
        log.info("{}, {}, {},{}, {}, {}", firstName, lastName, dateOfBirth, email, postcode, mobile);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dob = new Date();
        if (!StringUtil.isEmpty(dateOfBirth)) dob = dateFormat.parse(dateOfBirth);
        LocalDate dateThreeTenFormat = Instant.ofEpochMilli(dob.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        for (Person p: persons) {
            if (email.startsWith("gamstop") && firstName.startsWith("p")) return true;
            if(p.getFirstName().equalsIgnoreCase(firstName) &&
                    p.getLastName().equalsIgnoreCase(lastName) &&
                    p.getEmail().equalsIgnoreCase(email) &&
                    p.getPostcode().equalsIgnoreCase(postcode) &&
                    p.getMobile().equalsIgnoreCase(mobile) &&
                    p.getDateOfBirth().compareTo(dateThreeTenFormat)==0){
                return true;
            }
        }
        return false;
    }
    public boolean hasSEhistory(Person person) throws ParseException {
        for (Person p: this.persons) {
            log.info("------->Person------{}", person);
            log.info("------->P-----------{}", p);
            if (person.getEmail().startsWith("gamstop") && person.getFirstName().startsWith("P")) {
                return true;
            }
            if (p.equals(person)){
                return true;
            }
        }
        return false;
    }
    private Person defaultPerson(){
        Person bp = new Person();
        bp.setFirstName("Daniel");
        bp.setLastName("Pasquier");
        bp.setDateOfBirth(LocalDate.parse("1945-05-29"));
        bp.setEmail("umaury@blanc.org");
        bp.setPostcode("PE166RY");
        bp.setMobile("07700900000");
        return bp;
    }

    public Persons resetBlockedPersons() {
       this.persons = new Persons();
       this.persons.add(defaultPerson());
       return this.persons;
    }
}
