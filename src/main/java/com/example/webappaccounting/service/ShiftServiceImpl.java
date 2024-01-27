package com.example.webappaccounting.service;

import com.example.webappaccounting.model.Shift;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.ArrayList;

@Service("jpaShiftService")
@Transactional
public class ShiftServiceImpl implements ShiftService{

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Shift save(Shift shift) {
        if (shift.getId() == null) {
            entityManager.persist(shift);
        } else {
            entityManager.merge(shift);
        }
        return shift;
    }

    @Override
    public void delete(Shift shift) {
        Shift mergeShift = entityManager.merge(shift);
        entityManager.remove(mergeShift);
    }

    public void saveAll(ArrayList<Shift> shifts) {
        for (Shift shift : shifts) {
            save(shift);
        }
    }

    public void deleteAll(ArrayList<Shift> shifts) {
        for (Shift shift : shifts) {
            delete(shift);
        }
    }
}
