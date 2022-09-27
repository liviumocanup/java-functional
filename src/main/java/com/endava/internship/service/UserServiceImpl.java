package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.AbstractMap.SimpleEntry;

public class UserServiceImpl implements UserService {

    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users.stream()
                .map(User::getFirstName)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        return users.stream()
                .sorted(Comparator.comparing(User::getAge).reversed()
                        .thenComparing(User::getFirstName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        return users.stream()
                .map(User::getPrivileges)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        return users.stream()
                .filter(user -> user.getPrivileges().contains(Privilege.UPDATE) && user.getAge() > age)
                .findFirst();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(user -> user.getPrivileges().size()));
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        return users.stream()
                .mapToInt(User::getAge)
                .average().orElse(-1);
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        Map<String, Long> temp = users.stream()
                .map(User::getLastName)
                .collect(Collectors.groupingBy(a -> a, Collectors.counting()));

        Optional<Map.Entry<String, Long>> maxEntry = temp.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        long l = temp.entrySet().stream().filter(e -> e.getValue().equals(maxEntry.get().getValue())).count();
        return l <= 1 ? maxEntry.map(Map.Entry::getKey) : Optional.empty();
    }


    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        return users.stream()
                .filter(Arrays.stream(predicates).reduce(x -> true, Predicate::and))
                .collect(Collectors.toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        return users.stream()
                .map(mapFun)
                .collect(Collectors.joining(delimiter));
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream()
                        .map(p -> new SimpleEntry<>(p, user)))
                .collect(Collectors.groupingBy(SimpleEntry::getKey,
                        Collectors.mapping(SimpleEntry::getValue, Collectors.toList())));
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(User::getLastName, Collectors.counting()));
    }
}
