package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.ToIntFunction;

public class StatisticDao {
    protected Provider<EntityManager> entityManager;

    @Inject
    public StatisticDao(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
    }

    public Map<String, IncomeOutcome> getYearlyStatistic() {
        EntityManager em = entityManager.get();
        List<Object[]> res = em.createNativeQuery("" +
                "select cast(strftime('%Y', Operation.date/1000, 'unixepoch') as varchar), cast(TopCategory.operationType as int), cast(coalesce(sum(Operation.amount), 0) as double) " +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "group by strftime('%Y', Operation.date/1000, 'unixepoch'), TopCategory.operationType")
                .getResultList();
        Map<String, IncomeOutcome> results = new HashMap<>();
        for(Object[] row: res) {
            if(!results.containsKey(row[0])) {
                IncomeOutcome temp = new IncomeOutcome();
                results.put((String) row[0], temp);
            }
            if((int)row[1] == 0) {
                results.get(row[0]).income = BigDecimal.valueOf((double)row[2]);
            } else {
                results.get(row[0]).outcome = BigDecimal.valueOf((double)row[2]);
            }
        }

        return results;
    }

    public static String mapMonthToName(String month) {
        switch (month) {
            case "01":
                return "Jan";
            case "02":
                return "Feb";
            case "03":
                return "Mar";
            case "04":
                return "Apr";
            case "05":
                return "May";
            case "06":
                return "Jun";
            case "07":
                return "Jul";
            case "08":
                return "Aug";
            case "09":
                return "Sep";
            case "10":
                return "Oct";
            case "11":
                return "Nov";
            case "12":
                return "Dec";
            default:
                return "";
        }
    }

    public static String mapMonthNameToMonth(String month) {
        switch (month) {
            case "Jan":
                return "01";
            case "Feb":
                return "02";
            case "Mar":
                return "03";
            case "Apr":
                return "04";
            case "May":
                return "05";
            case "Jun":
                return "06";
            case "Jul":
                return "07";
            case "Aug":
                return "08";
            case "Sep":
                return "09";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
            default:
                return "";
        }
    }

    public Map<String, BigDecimal> getTopCategoryOutcomeInYear(String year) {
        EntityManager em = entityManager.get();
        List<Object[]> res = em.createNativeQuery("" +
                "select cast(TopCategory.name as varchar), cast(coalesce(sum(Operation.amount), 0) as double) " +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "where " +
                "   strftime('%Y', Operation.date/1000, 'unixepoch') = :year " +
                "   and TopCategory.operationType = 1 " +
                "group by TopCategory.name ")
                .setParameter("year", year)
                .getResultList();

        Map<String, BigDecimal> results = new HashMap<>();
        for(Object[] row: res) {
            String key = (String) row[0];
            results.put(key, BigDecimal.valueOf((double)row[1]));
        }
        return results;
    }

    public Map<String, BigDecimal> getTopCategoryOutcomeInMonth(String year, String month) {
        EntityManager em = entityManager.get();
        List<Object[]> res = em.createNativeQuery("" +
                "select cast(TopCategory.name as varchar), cast(coalesce(sum(Operation.amount), 0) as double) " +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "where " +
                "   strftime('%Y', Operation.date/1000, 'unixepoch') = :year " +
                "   and strftime('%m', Operation.date/1000, 'unixepoch') = :month " +
                "   and TopCategory.operationType = 1 " +
                "group by TopCategory.name ")
                .setParameter("year", year)
                .setParameter("month", month)
                .getResultList();

        Map<String, BigDecimal> results = new HashMap<>();
        for(Object[] row: res) {
            String key = (String) row[0];
            results.put(key, BigDecimal.valueOf((double)row[1]));
        }
        return results;
    }

    public Map<String, IncomeOutcome> getMonthlyStatistic(String year) {
        EntityManager em = entityManager.get();
        List<Object[]> res = em.createNativeQuery("" +
                "select cast(strftime('%m', Operation.date/1000, 'unixepoch') as varchar), cast(TopCategory.operationType as int), cast(coalesce(sum(Operation.amount), 0) as double) " +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "where strftime('%Y', Operation.date/1000, 'unixepoch') = :year " +
                "group by strftime('%m', Operation.date/1000, 'unixepoch'), TopCategory.operationType")
                .setParameter("year", year)
                .getResultList();

        Map<String, IncomeOutcome> results = new HashMap<>();
        for(Object[] row: res) {
            String key = (String) row[0];
            if(!results.containsKey(key)) {
                IncomeOutcome temp = new IncomeOutcome();
                results.put(key, temp);
            }
            if((int)row[1] == 0) {
                results.get(key).income = BigDecimal.valueOf((double)row[2]);
            } else {
                results.get(key).outcome = BigDecimal.valueOf((double)row[2]);
            }
        }

        return results;
    }

    public List<String> getAvailableHistoryYears() {
        EntityManager em = entityManager.get();
        List<Object[]> res = em.createNativeQuery("" +
                "select cast(strftime('%Y', Operation.date/1000, 'unixepoch') as varchar)" +
                "from Operation " +
                "group by strftime('%Y', Operation.date/1000, 'unixepoch')")
                .getResultList();
        List<String> results = new ArrayList<>();
        for(Object row: res) {
            results.add((String) row);
        }
        results.sort(Comparator.comparingInt((ToIntFunction<String>) Integer::parseInt).reversed());
        return results;
    }

    public List<String> getAvailableOutcomeHistoryYears() {
        EntityManager em = entityManager.get();
        List<Object[]> res = em.createNativeQuery("" +
                "select cast(strftime('%Y', Operation.date/1000, 'unixepoch') as varchar)" +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "where TopCategory.operationType=1 " +
                "group by strftime('%Y', Operation.date/1000, 'unixepoch')")
                .getResultList();
        List<String> results = new ArrayList<>();
        for(Object row: res) {
            results.add((String) row);
        }
        results.sort(Comparator.comparingInt((ToIntFunction<String>) Integer::parseInt).reversed());
        return results;
    }

    public List<String> getAvailableOutcomeHistoryMonthsInYear(String year) {
        EntityManager em = entityManager.get();
        List<Object[]> res = em.createNativeQuery("" +
                "select cast(strftime('%m', Operation.date/1000, 'unixepoch') as varchar)" +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "where TopCategory.operationType=1 " +
                "and strftime('%Y', Operation.date/1000, 'unixepoch') = :year " +
                "group by strftime('%m', Operation.date/1000, 'unixepoch')")
                .setParameter("year", year)
                .getResultList();
        List<String> results = new ArrayList<>();
        for(Object row: res) {
            results.add((String) row);
        }
        results.sort(Comparator.comparingInt((ToIntFunction<String>) Integer::parseInt).reversed());
        return results;
    }

    public class IncomeOutcome {
        public BigDecimal income = BigDecimal.ZERO;
        public BigDecimal outcome = BigDecimal.ZERO;
    }
}
