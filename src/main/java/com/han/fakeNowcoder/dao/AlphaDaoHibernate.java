package com.han.fakeNowcoder.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaDaoHibernate")
public class AlphaDaoHibernate implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
