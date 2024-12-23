package ru.skillbox.mc_account.service;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import ru.skillbox.mc_account.entity.Account;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public class AccountSpecifications {
    public static Specification<Account> searchByKeywords(String[] keywords) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            for (String keyword : keywords) {
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + keyword.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + keyword.toLowerCase() + "%"), // Добавлена проверка для lastName
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + keyword.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + keyword.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), "%" + keyword.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + keyword.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("about")), "%" + keyword.toLowerCase() + "%")
                );

                predicate = criteriaBuilder.and(predicate, keywordPredicate);
            }
            return predicate;
        };
    }

    public static Specification<Account> isDeleted(boolean isDeleted) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("deleted"), isDeleted);
            return predicate;
        };
    }

    public static Specification<Account> byIds(UUID[] ids) {
        return (root, query, criteriaBuilder) -> {
            if (ids == null || ids.length == 0) {
                return null;
            }

            Predicate predicate = root.get("id").in((Object[]) ids);
            return predicate;
        };
    }

    public static Specification<Account> byAgeRange(Integer ageFrom, Integer ageTo) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            LocalDate today = LocalDate.now(ZoneId.systemDefault());

            if (ageFrom != null) {
                LocalDate fromDate = today.minusYears(ageFrom);
                Instant fromInstant = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), fromInstant));
            }

            if (ageTo != null) {
                LocalDate toDate = today.minusYears(ageTo);
                Instant toInstant = toDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), toInstant));
            }

            return predicate;
        };
    }

    public static Specification<Account> byCountry(String country) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("country")), country.toLowerCase());
    }

    public static Specification<Account> byCity(String city) {
        if (city == null || city.isEmpty()) {
            return null;
        } else {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("city")), city.toLowerCase());
        }
    }

    public static Specification<Account> byFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> {
            if (firstName == null || firstName.isEmpty()) {
                return null;
            }
            Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
            return predicate;
        };
    }

    public static Specification<Account> byLastName(String lastName) {
        return (root, query, criteriaBuilder) -> {
            if (lastName == null || lastName.isEmpty()) {
                return null;
            }
            Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
            return predicate;
        };
    }

    public static Specification<Account> byFriendUuidsAndFirstName(List<UUID> friendUuids, String firstName) {
        return (root, query, criteriaBuilder) -> {
            if (friendUuids == null || friendUuids.isEmpty() || firstName == null || firstName.isEmpty()) {
                return null;
            }
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<Account> friendRoot = subquery.from(Account.class);
            subquery.select(friendRoot.get("id"))
                    .where(
                            criteriaBuilder.and(
                                    friendRoot.get("id").in(friendUuids),
                                    criteriaBuilder.like(criteriaBuilder.lower(friendRoot.get("firstName")), "%" + firstName.toLowerCase() + "%")
                            )
                    );
            return root.get("id").in(subquery);
        };
    }
}
