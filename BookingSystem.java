import java.util.ArrayList;
import java.util.Scanner;

class Movie {
    String title;
    int availableSeats;

    Movie(String title, int availableSeats) {
        this.title = title;
        this.availableSeats = availableSeats;
    }
}

public class BookingSystem {
    private ArrayList<Movie> movies;
    private Movie currentMovie;
    private ArrayList<Integer> selectedSeats;

    public BookingSystem() {
        movies = new ArrayList<>();
        selectedSeats = new ArrayList<>();
    }

    public void addMovie(String title, int availableSeats) {
        Movie movie = new Movie(title, availableSeats);
        movies.add(movie);
    }

    public void displayMovies() {
        System.out.println("Available Movies:");
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + ". " + movies.get(i).title);
        }
    }

    public void selectMovie(int index) {
        if (index >= 0 && index < movies.size()) {
            currentMovie = movies.get(index);
            selectedSeats.clear();
            System.out.println("You've selected " + currentMovie.title);
        } else {
            System.out.println("Invalid movie selection.");
        }
    }

    public void displaySeats() {
        System.out.println("Available Seats:");
        for (int i = 1; i <= currentMovie.availableSeats; i++) {
            if (!selectedSeats.contains(i)) {
                System.out.print(i + " ");
            } else {
                System.out.print("X ");
            }

            if (i % 10 == 0) {
                System.out.println();
            }
        }
    }

    public void selectSeat(int seat) {
        if (seat >= 1 && seat <= currentMovie.availableSeats) {
            if (!selectedSeats.contains(seat)) {
                selectedSeats.add(seat);
                System.out.println("Seat " + seat + " selected.");
            } else {
                System.out.println("Seat already selected.");
            }
        } else {
            System.out.println("Invalid seat selection.");
        }
    }

    public void bookTickets() {
        if (!selectedSeats.isEmpty()) {
            System.out.println("Tickets booked successfully!");
            currentMovie.availableSeats -= selectedSeats.size();
            currentMovie = null;
            selectedSeats.clear();
        } else {
            System.out.println("No seats selected. Please choose seats before booking.");
        }
    }

    public static void main(String[] args) {
        BookingSystem bookingSystem = new BookingSystem();
        bookingSystem.addMovie("Avengers:Infinity War", 50);
        bookingSystem.addMovie("Avatar", 45);
        System.out.println("------------------------------------------------");

        bookingSystem.displayMovies();
        System.out.println("--------------------------------");
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Select a movie (enter the corresponding number): ");
        int movieChoice = scanner.nextInt();
        bookingSystem.selectMovie(movieChoice - 1);

        bookingSystem.displaySeats();

        System.out.print("Select a seat: ");
        int seatChoice = scanner.nextInt();
        bookingSystem.selectSeat(seatChoice);

        bookingSystem.bookTickets();
        scanner.close();
    }
}
