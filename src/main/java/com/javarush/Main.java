package com.javarush;

import com.javarush.dao.*;
import com.javarush.domain.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import javax.security.auth.login.CredentialNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Main {
    private final SessionFactory sessionFactory;

    private ActorDAO actorDAO;
    private AddressDAO addressDAO;
    private CategoryDAO categoryDAO;
    private CityDAO cityDAO;
    private CountryDAO countryDAO;
    private CustomerDAO customerDAO;
    private FilmDAO filmDAO;
    private FilmTextDAO filmTextDAO;
    private InventoryDAO inventoryDAO;
    private LanguageDAO languageDAO;
    private PaymentDAO paymentDAO;
    private RentalDAO rentalDAO;
    private StaffDAO staffDAO;
    private StoreDAO storeDAO;

    public Main() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/movie");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "romsul21");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "validate");

        sessionFactory = new Configuration()
                .addAnnotatedClass(Actor.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Category.class)
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Film.class)
                .addAnnotatedClass(FilmText.class)
                .addAnnotatedClass(Inventory.class)
                .addAnnotatedClass(Language.class)
                .addAnnotatedClass(Payment.class)
                .addAnnotatedClass(Rental.class)
                .addAnnotatedClass(Staff.class)
                .addAnnotatedClass(Store.class)
                .setProperties(properties)
                .buildSessionFactory();

        actorDAO = new ActorDAO(sessionFactory);
        addressDAO = new AddressDAO(sessionFactory);
        categoryDAO = new CategoryDAO(sessionFactory);
        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);
        customerDAO = new CustomerDAO(sessionFactory);
        filmDAO = new FilmDAO(sessionFactory);
        filmTextDAO = new FilmTextDAO(sessionFactory);
        inventoryDAO = new InventoryDAO(sessionFactory);
        languageDAO = new LanguageDAO(sessionFactory);
        paymentDAO = new PaymentDAO(sessionFactory);
        rentalDAO = new RentalDAO(sessionFactory);
        staffDAO = new StaffDAO(sessionFactory);
        storeDAO = new StoreDAO(sessionFactory);
    }

    public static void main(String[] args) {
        Main main = new Main();
        Customer customer = main.createCustomer();
        main.returnInventoryToStore();
        main.customerRentInventory(customer);
        main.newFilmCameOut();
    }

    private void newFilmCameOut() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Language filmLanguage = languageDAO.getItems(0, 5).stream()
                    .unordered().findAny().get();
            Set<Actor> actors = new HashSet<>(actorDAO.getItems(0, 7));
            Set<Category> categories = new HashSet<>(categoryDAO.getItems(0, 3));

            Film film = new Film();
            film.setActors(actors);
            film.setRating(Rating.PG);
            film.setSpecialFeatures(Set.of(Feature.DELETED_SCENES, Feature.BEHIND_THE_SCENES, Feature.TRAILERS));
            film.setLength((short) 90);
            film.setReplacementCost(BigDecimal.TEN);
            film.setRentalRate(BigDecimal.ONE);
            film.setLanguage(filmLanguage);
            film.setDescription("Best seller");
            film.setTitle("JavaRush");
            film.setRentalDuration((byte) 22);
            film.setOriginalLanguage(filmLanguage);
            film.setCategories(categories);
            film.setYear(Year.of(2022));

            filmDAO.save(film);

            FilmText filmText = new FilmText();
            filmText.setId(film.getId());
            filmText.setFilm(film);
            filmText.setDescription("Story about studying with JavaRush online Java course");
            filmText.setTitle("JavaRush");
            filmTextDAO.save(filmText);

            session.getTransaction().commit();
        }
    }

    private void customerRentInventory(Customer customer) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Film film = filmDAO.getFirstAvailableFilmForRent();
            Store store = storeDAO.getItems(0, 1).get(0);

            Inventory inventory = new Inventory();
            inventory.setFilm(film);
            inventory.setStore(store);
            inventoryDAO.save(inventory);

            Staff staff = store.getStaff();

            Rental rental = new Rental();
            rental.setRentalDate(LocalDateTime.now());
            rental.setInventory(inventory);
            rental.setStaff(staff);
            rental.setCustomer(customer);
            rentalDAO.save(rental);

            Payment payment = new Payment();
            payment.setCustomer(customer);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setRental(rental);
            payment.setAmount(BigDecimal.valueOf(100.99));
            payment.setStaff(staff);
            paymentDAO.save(payment);

            session.getTransaction().commit();
        }
    }

    private void returnInventoryToStore() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Rental rental = rentalDAO.getAnyUnreturnedRental();
            rental.setReturnDate(LocalDateTime.now());
            rentalDAO.save(rental);

            session.getTransaction().commit();
        }
    }

    private Customer createCustomer() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Store store = storeDAO.getItems(0, 1).get(0);
            City city = cityDAO.getByName("Barcelona");

            Address address = new Address();
            address.setAddress("Solnnechnaya str, 58");
            address.setCity(city);
            address.setPhone("029-666-888");
            address.setDistrict("Football academy");

            addressDAO.save(address);

            Customer customer = new Customer();
            customer.setAddress(address);
            customer.setEmail("freeBelarus@luka.by");
            customer.setFirstName("Vasil");
            customer.setLastName("Vasilev");
            customer.setStore(store);
            customer.setActive(true);

            customerDAO.save(customer);

            session.getTransaction().commit();
            return customer;
        }
    }
}
