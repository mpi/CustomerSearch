package tdd.customerSearch;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:jdbc-context.xml")
public class CustomerDaoTest {

    @Autowired
    private JdbcTemplate template;
    
    private CustomerDao dao;
    
    @Before
    public void setUp() {
        dao = new CustomerDao(template);
    }

    @Test
    public void shouldHaveTemplate() throws Exception {

        // given:
        customer("1", "Name1", Boolean.TRUE, "2012-05-01");
        customer("2", "Name2", Boolean.FALSE, "2012-01-01");
        customer("3", "Name3", Boolean.TRUE, "2012-05-10");
        customer("4", "Name4", Boolean.FALSE, "2013-05-01");
        
        // when:
        CustomerCriteria criteria = new CustomerCriteria();
        criteria.setActive(Boolean.TRUE);
        criteria.setRegistrationFrom(date("2012-05-05"));
        
        List<Customer> result = dao.getCustomersByCriteria(criteria);
        
        // then:
        assertThat(extractProperty("name").from(result)).containsExactly("Name3");
    }

    // --
    
    private void customer(String num, String name, Boolean active, String date) {
        
        String sql = String.format(
                "INSERT INTO customer(customer_num, name, active, registration_dt) VALUES ('%s', '%s', '%s', PARSEDATETIME('%s', 'yyyy-MM-dd'))",
                num, name, (active ? "Y" : "N"), date);
        template.execute(sql);
        System.out.println(sql);

    }

    private Date date(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }
}
