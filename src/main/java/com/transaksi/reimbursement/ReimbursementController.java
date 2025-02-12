package com.transaksi.reimbursement;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Optional;


@Controller
public class ReimbursementController {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private BisnisRepository bisnisRepository;

    @Autowired
    private ReimbursementRepository reimbursementRepository;

    @Autowired
    private KantorRepository kantorRepository;

    @Autowired
    private MedicalRepository medicalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    // Method to prevent caching
    private void preventCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    // Redirect root to login-admin
    @GetMapping("/")
    public String redirectToLoginPage() {
        return "redirect:/login-admin";  // Redirect to admin login by default
    }

    // Admin Login
    @GetMapping("/login-admin")
    public String showAdminLoginPage(HttpSession session, HttpServletResponse response) {
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        if (loggedIn != null && loggedIn) {
            return "redirect:/dashboard";
        }

//        // Prevent caching
//        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//        response.setHeader("Pragma", "no-cache");
//        response.setDateHeader("Expires", 0);

        preventCaching(response);
        return "login-admin";
    }


    @PostMapping("/login-admin")
    public String loginAdmin(@RequestParam String username, @RequestParam String password, HttpSession session) {
//        List<User> users = List.of(
//                new User("admin", "adminpass")
////                new User("user2", "password2"),
////                new User("user3", "password3")
//        );

        Optional<com.transaksi.reimbursement.Admin> admin = adminRepository.findByUsername(username);

        if (admin.isPresent() && admin.get().getPassword().equals(password) && "ADMIN".equals(admin.get().getRole())) {
            session.setAttribute("loggedIn", true);
            session.setAttribute("username", username);
            session.setAttribute("role", "ADMIN");
            return "redirect:/dashboard";
        }

//        boolean isValidUser = users.stream()
//                .anyMatch(user -> user.getUsername().equals(username) && user.getPassword().equals(password));

//        if ("admin".equals(username) && "adminpass".equals(password)) {
//        if (isValidUser) {
//            session.setAttribute("loggedIn", true);
//            session.setAttribute("username", username);
//            session.setAttribute("role", "ADMIN"); // Set the role to "ADMIN"
//            return "redirect:/dashboard";
//
//        }
        session.invalidate();
        return "redirect:/login-admin?error=true";
    }


    // User Login Page
    @GetMapping("/login-user")
    public String showUserLoginPage(HttpSession session, HttpServletResponse response) {
        Boolean userLoggedIn = (Boolean) session.getAttribute("userLoggedIn");
        if (userLoggedIn != null && userLoggedIn) {
            return "redirect:/reimbursement";
        }

//        // Prevent caching
//        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//        response.setHeader("Pragma", "no-cache");
//        response.setDateHeader("Expires", 0);

        preventCaching(response);

        return "login-user";
    }


    @PostMapping("/login-user")
    public String loginUser(@RequestParam String username, @RequestParam String password, HttpSession session) {
//        List<User> users = List.of(
//                new User("user1", "password1"),
//                new User("user2", "password2"),
//                new User("user3", "password3"),
//                new User("user4", "password4"),
//                new User("user5", "password5")
//        );

        Optional<com.transaksi.reimbursement.User> user = userRepository.findByUsername(username);

        if (user.isPresent() && user.get().getPassword().equals(password) && "USER".equals(user.get().getRole())) {
            session.setAttribute("userLoggedIn", true);
            session.setAttribute("username", username);
            session.setAttribute("role", "USER");
            return "redirect:/reimbursement";
        }

//        boolean isValidUser = users.stream()
//                .anyMatch(user -> user.getUsername().equals(username) && user.getPassword().equals(password));
//
//        if (isValidUser) {
//            session.setAttribute("userLoggedIn", true);
//            session.setAttribute("username", username);
//            session.setAttribute("role", "USER"); // Set the role to "USER"
//            return "redirect:/reimbursement";
//        }
        session.invalidate();
        return "redirect:/login-user?error=true";
    }


    // User model to store username and password
    public static class User {
        private String username;
        private String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }


    // Reimbursement Form
    @GetMapping("/reimbursement")
    public String showReimbursementForm(HttpSession session, HttpServletResponse response, Model model) {
        Boolean userLoggedIn = (Boolean) session.getAttribute("userLoggedIn");
        if (userLoggedIn == null || !userLoggedIn) {
            return "redirect:/login-user";
        }

//        // Prevent caching
//        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//        response.setHeader("Pragma", "no-cache");
//        response.setDateHeader("Expires", 0);

        preventCaching(response);

        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username != null ? username : "User");
        return "reimbursement";
    }


    @PostMapping("/reimbursement")
    public String submitReimbursement(@RequestParam String name,
                                      @RequestParam double amount,
                                      @RequestParam String reason,
                                      @RequestParam String type,
                                      Model model) {
        // Validasi jumlah maksimal berdasarkan tipe reimbursement
        switch (type) {
            case "Bisnis":
                if (amount > 3000000) {
                    model.addAttribute("error", "Amount for 'Bisnis' exceeds the maximum limit of 3,000,000");
                    return "reimbursement"; // Kembali ke form dengan pesan kesalahan
                }
                Bisnis bisnis = new Bisnis();
                bisnis.setName(name);
                bisnis.setAmount(amount);
                bisnis.setReason(reason);
                bisnisRepository.save(bisnis);
                break;

            case "Kantor":
                if (amount > 2000000) {
                    model.addAttribute("error", "Amount for 'Kantor' exceeds the maximum limit of 2,000,000");
                    return "reimbursement"; // Return to the form with an error message
                }
                Kantor kantor = new Kantor();
                // Ensure ID is set if required
                kantor.setName(name);
                kantor.setAmount(amount);
                kantor.setReason(reason);
                kantorRepository.save(kantor);
                break;


            case "Medical":
                if (amount > 1000000) {
                    model.addAttribute("error", "Amount for 'Medical' exceeds the maximum limit of 1,000,000");
                    return "reimbursement"; // Kembali ke form dengan pesan kesalahan
                }
                Medical medical = new Medical();
                medical.setName(name);
                medical.setAmount(amount);
                medical.setReason(reason);
                medicalRepository.save(medical);
                break;

            default:
                model.addAttribute("error", "Invalid reimbursement type: " + type);
                return "reimbursement"; // Kembali ke form dengan pesan kesalahan
        }

        return "redirect:/reimbursement"; // Redirect ke halaman reimbursement setelah berhasil
    }


    // Method to check if the user is a "user" logged in
    private boolean isUserLoggedIn(HttpSession session) {
        Boolean userLoggedIn = (Boolean) session.getAttribute("userLoggedIn");
        return userLoggedIn != null && userLoggedIn;
    }

    private boolean isAdminLoggedIn(HttpSession session) {
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        return loggedIn != null && loggedIn;
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, HttpServletResponse response, Model model) {
        preventCaching(response);

        String role = (String) session.getAttribute("role"); // Ambil peran pengguna dari session
        String username = (String) session.getAttribute("username"); // Get the username from session
        if (role == null || (!role.equals("ADMIN") && !role.equals("USER"))) {
            return "redirect:/login-admin"; // Redirect jika tidak ada peran
        }

        List<Dashboard> dashboards = dashboardRepository.findAll();
        if ("ADMIN".equals(role)) {
            // Admin can see all dashboard entries
            dashboards = dashboardRepository.findAll();
        } else if ("USER".equals(role)) {
            // User can only see their own entries
            dashboards = dashboardRepository.findByName(username);
        } else {
            dashboards = List.of(); // No entries if role is invalid
        }

        double totalBudget = dashboards.stream().mapToDouble(Dashboard::getAmount).sum();
        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("dashboards", dashboards);
        model.addAttribute("isAdmin", role.equals("ADMIN")); // Kirim informasi role
        return "dashboard";
    }


    @PostMapping("/dashboard/update-status")
    @ResponseBody
    public Dashboard updateDashboardStatus(@RequestParam Long id, @RequestParam String status) {
        Dashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        dashboard.setStatus(status);
        dashboardRepository.save(dashboard);
        return dashboard;
    }


    @PostMapping("/dashboard/edit")
    public String editDashboard(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam double amount,
                                @RequestParam String reason,
                                @RequestParam String status) {
        Dashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        dashboard.setName(name);
        dashboard.setAmount(amount);
        dashboard.setReason(reason);
        dashboard.setStatus(status); // Update status
        dashboardRepository.save(dashboard);
        return "redirect:/dashboard";
    }

    @DeleteMapping("/dashboard/delete/{id}")
    @ResponseBody
    public String deleteDashboard(@PathVariable Long id) {
        dashboardRepository.deleteById(id);
        return "Deleted";
    }


    @GetMapping("/dashboard/{id}")
    @ResponseBody
    public Dashboard getDashboardById(@PathVariable Long id) {
        return dashboardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
    }

    //export
    // Metode untuk mencegah caching
//    private void preventCaching(HttpServletResponse response) {
//        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//        response.setHeader("Pragma", "no-cache");
//        response.setDateHeader("Expires", 0);
//    }

    // Halaman untuk memilih format ekspor
    @GetMapping("/dashboard/export")
    public String showExportOptions() {
        return "export-options"; // Tampilkan halaman dengan pilihan format ekspor
    }

    @GetMapping("/dashboard/export/{format}")
    public void exportDashboard(@PathVariable String format, HttpServletResponse response) throws Exception {
        switch (format.toLowerCase()) {

            case "pdf":
                exportToPDF(response);
                break;

            default:
                throw new IllegalArgumentException("Format tidak didukung: " + format);
        }
    }

    private void exportToPDF(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=dashboard_report.pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Title centered at the top
        Paragraph title = new Paragraph("FORM BIAYA REIMBURSEMENT", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Space
        document.add(new Paragraph(" "));

        // Left header: BENUA KERTAS
        Paragraph leftHeader = new Paragraph("PT BENUA KERTAS", FontFactory.getFont(FontFactory.HELVETICA, 12));
        leftHeader.setAlignment(Element.ALIGN_LEFT);
        document.add(leftHeader);

        // Right header: No Pol Mobil, Nama Supir
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new int[]{1, 2});

        document.add(headerTable);

        // Space
        document.add(new Paragraph(" "));

        // Data table from Dashboard
        PdfPTable dataTable = new PdfPTable(6); // Updated to include all columns
        dataTable.setWidthPercentage(100);
        dataTable.setWidths(new int[]{2, 2, 2, 2, 3, 4}); // Adjusted column widths for better spacing

        // Table headers
        dataTable.addCell(getHeaderCell("Date"));
        dataTable.addCell(getHeaderCell("Name"));
        dataTable.addCell(getHeaderCell("Type"));
        dataTable.addCell(getHeaderCell("Amount"));
        dataTable.addCell(getHeaderCell("Tujuan"));
        dataTable.addCell(getHeaderCell("Status"));

        // Fetch data from the repository
        List<Dashboard> dashboards = dashboardRepository.findAll();
        for (Dashboard dashboard : dashboards) {
            // Adding rows to the data table
            dataTable.addCell(getCell("", Element.ALIGN_CENTER));  // Empty cell for Date
            dataTable.addCell(getCell(dashboard.getName(), Element.ALIGN_LEFT));
            dataTable.addCell(getCell(dashboard.getType(), Element.ALIGN_LEFT));
            dataTable.addCell(getCell(String.valueOf(dashboard.getAmount()), Element.ALIGN_RIGHT));
            dataTable.addCell(getCell(dashboard.getReason(), Element.ALIGN_LEFT));
            dataTable.addCell(getCell(dashboard.getStatus(), Element.ALIGN_CENTER));
        }

        document.add(dataTable);

        // Space
        document.add(new Paragraph(" "));

        // Signature section at the bottom
        PdfPTable signatureTable = new PdfPTable(3);
        signatureTable.setWidthPercentage(100);
        signatureTable.setWidths(new int[]{1, 1, 1});
        signatureTable.addCell(getCell("Diperiksa Oleh\n\n\n(........................)", Element.ALIGN_CENTER));
        signatureTable.addCell(getCell("Penanggung Jawab\n\n\n(........................)", Element.ALIGN_CENTER));
        signatureTable.addCell(getCell("Disetujui Oleh\n\n\n(........................)", Element.ALIGN_CENTER));
        document.add(signatureTable);

        document.close();
    }

    // Utility method to create a cell
    private PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(8); // Increased padding for better readability
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private PdfPCell getHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cell.setPadding(8);  // Increased padding for better readability
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        return cell;
    }


    //end export

//end Kantor code

    //start bisnis code

    @GetMapping("/bisnis")
    public String showBisnis(Model model, HttpSession session) {
        Boolean userLoggedIn = (Boolean) session.getAttribute("userLoggedIn");
        List<Bisnis> bisnisList = bisnisRepository.findAll();
        double totalBudget = bisnisList.stream()
                .mapToDouble(Bisnis::getAmount)
                .sum();

        model.addAttribute("bisnis", bisnisList);
        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("userLoggedIn", userLoggedIn != null && userLoggedIn); // Add userLoggedIn attribute
        return "bisnis"; // Return view
    }



    @PostMapping("/bisnis/update-status")
    public String updateBisnisStatus(@RequestParam Long id) {
        Bisnis bisnis = bisnisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        // Logic to set the status based on the amount
        if (bisnis.getAmount() > 1000000) {
            bisnis.setStatus("Ditolak Pengajuan");
        } else {
            bisnis.setStatus("Data Sesuai");
        }

        bisnisRepository.save(bisnis);
        return "redirect:/bisnis";
    }

    @PostMapping("/bisnis/submit-all")
    public String submitAllBisnis(Model model) {
        List<Bisnis> bisnisList = bisnisRepository.findAll();
        double totalBudget = bisnisList.stream()
                .mapToDouble(Bisnis::getAmount)
                .sum();

        if (totalBudget > 1000000) {
            model.addAttribute("error", "Ditolak karena melebihi budget max 1,000,000");
            return "bisnis"; // Return to the same page with error message
        }

        // Save all bisnis entries to the dashboard
        for (Bisnis bisnis : bisnisList) {
            Dashboard dashboard = new Dashboard();
            dashboard.setName(bisnis.getName());
            dashboard.setAmount(bisnis.getAmount());
            dashboard.setReason(bisnis.getReason());
            dashboard.setType("Bisnis");  // Set the type to "Bisnis"
            dashboardRepository.save(dashboard);
        }

        // Delete all the bisnis entries after saving them to the dashboard
        bisnisRepository.deleteAll();

        // Add success message to model
        model.addAttribute("successMessage", "Data berhasil dikirim dan Data sudah dihapus");

        return "redirect:/dashboard"; // Redirect to the dashboard page after successful submission
    }

    // Edit Bisnis (Only "User" can edit)
    @PostMapping("/bisnis/edit")
    public String editBisnis(@RequestParam Long id, @RequestParam String name, @RequestParam double amount, @RequestParam String reason, HttpSession session, Model model) {
        if (!isUserLoggedIn(session)) {
            model.addAttribute("error", "Edit or delete cannot be used by other users.");
            List<Bisnis> bisnisList = bisnisRepository.findAll(); // Add the list of 'Bisnis' to the model
            model.addAttribute("bisnis", bisnisList);
            return "bisnis"; // Redirect to the same page with error, but all tables are visible
        }

        Bisnis bisnis = bisnisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        String loggedInUser = (String) session.getAttribute("username");
        // Ensure the logged-in user is the one who owns the bisnis entry
        if (!bisnis.getName().equals(loggedInUser)) {
            model.addAttribute("error", "Edit or delete cannot be used by other users.");
            List<Bisnis> bisnisList = bisnisRepository.findAll(); // Add the list of 'Bisnis' to the model
            model.addAttribute("bisnis", bisnisList);
            return "bisnis"; // Redirect to the same page with error, but all tables are visible
        }

        bisnis.setName(name);
        bisnis.setAmount(amount);
        bisnis.setReason(reason);
        bisnisRepository.save(bisnis);
        return "redirect:/bisnis";
    }


    // Delete Bisnis (Only "User" can delete)
    @DeleteMapping("/bisnis/delete/{id}")
    @ResponseBody
    public String deleteBisnis(@PathVariable Long id, HttpSession session, Model model) {
        if (!isUserLoggedIn(session)) {
            return "Edit or delete cannot be used by other users.";  // Prevent non-users from deleting
        }

        Bisnis bisnis = bisnisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        String loggedInUser = (String) session.getAttribute("username");
        // Ensure the logged-in user is the one who owns the kantor entry
        if (!bisnis.getName().equals(loggedInUser)) {
            return "Edit or delete cannot be used by other users.";  // Prevent other users from deleting
        }

        bisnisRepository.deleteById(id);
        return "Deleted";
    }


    @GetMapping("/bisnis/{id}")
    @ResponseBody
    public Bisnis getBisnisById(@PathVariable Long id) {
        return bisnisRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
    }

//end Bisnis code

    //start Kantor code

    @GetMapping("/kantor")
    public String showKantor(Model model, HttpSession session) {
        Boolean userLoggedIn = (Boolean) session.getAttribute("userLoggedIn");
        List<Kantor> kantorList = kantorRepository.findAll();
        double totalBudget = kantorList.stream()
                .mapToDouble(Kantor::getAmount)
                .sum();

        model.addAttribute("kantor", kantorList);
        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("userLoggedIn", userLoggedIn != null && userLoggedIn); // Add userLoggedIn attribute
        return "kantor"; // Return view
    }


    @PostMapping("/kantor/submit-all")
    public String submitAllKantor(Model model) {
        List<Kantor> kantorList = kantorRepository.findAll();
        double totalBudget = kantorList.stream()
                .mapToDouble(Kantor::getAmount)
                .sum();

        if (totalBudget > 1000000) {
            model.addAttribute("error", "Ditolak karena melebihi budget max 1,000,000");
            return "kantor"; // Return to the same page with error message
        }

        // Save all kantor entries to the dashboard
        for (Kantor kantor : kantorList) {
            Dashboard dashboard = new Dashboard();
            dashboard.setName(kantor.getName());
            dashboard.setAmount(kantor.getAmount());
            dashboard.setReason(kantor.getReason());
            dashboard.setType("Kantor");  // Set the type to "Kantor"
            dashboardRepository.save(dashboard);
        }

        // Delete all the kantor entries after saving them to the dashboard
        kantorRepository.deleteAll();

        // Add success message to model
        model.addAttribute("successMessage", "Data berhasil dikirim dan Data sudah dihapus");

        return "redirect:/dashboard"; // Redirect to the dashboard page after successful submission
    }


    @PostMapping("/kantor/update-status")
    public String updateKantorStatus(@RequestParam Long id) {
        Kantor kantor = kantorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        // Logic to set the status based on the amount
        if (kantor.getAmount() > 1000000) {
            kantor.setStatus("Ditolak Pengajuan");
        } else {
            kantor.setStatus("Data Sesuai");
        }

        kantorRepository.save(kantor);
        return "redirect:/kantor";
    }


    // Edit Kantor (Only "User" can edit)
    @PostMapping("/kantor/edit")
    public String editKantor(@RequestParam Long id, @RequestParam String name, @RequestParam double amount, @RequestParam String reason, HttpSession session, Model model) {
        if (!isUserLoggedIn(session)) {
            model.addAttribute("error", "Edit or delete cannot be used by other users.");
            List<Kantor> kantorList = kantorRepository.findAll(); // Add the list of 'Bisnis' to the model
            model.addAttribute("kantor", kantorList);
            return "kantor"; // Redirect to the same page with error, but all tables are visible
        }

        Kantor kantor = kantorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        String loggedInUser = (String) session.getAttribute("username");
        // Ensure the logged-in user is the one who owns the bisnis entry
        if (!kantor.getName().equals(loggedInUser)) {
            model.addAttribute("error", "Edit or delete cannot be used by other users.");
            List<Kantor> kantorList = kantorRepository.findAll(); // Add the list of 'Kantor' to the model
            model.addAttribute("kantor", kantorList);
            return "kantor"; // Redirect to the same page with error, but all tables are visible
        }

        kantor.setName(name);
        kantor.setAmount(amount);
        kantor.setReason(reason);
        kantorRepository.save(kantor);
        return "redirect:/kantor";
    }

    // Delete Kantor (Only "User" can delete)
    @DeleteMapping("/kantor/delete/{id}")
    @ResponseBody
    public String deleteKantor(@PathVariable Long id, HttpSession session, Model model) {
        if (!isUserLoggedIn(session)) {
            return "Edit or delete cannot be used by other users.";  // Prevent non-users from deleting
        }

        Kantor kantor = kantorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        String loggedInUser = (String) session.getAttribute("username");
        // Ensure the logged-in user is the one who owns the bisnis entry
        if (!kantor.getName().equals(loggedInUser)) {
            return "Edit or delete cannot be used by other users.";  // Prevent other users from deleting
        }

        kantorRepository.deleteById(id);
        return "Deleted";
    }

    @GetMapping("/kantor/{id}")
    @ResponseBody
    public Kantor getKantorById(@PathVariable Long id) {
        return kantorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
    }

//end Kantor code

    //start Medical code

    @GetMapping("/medical")
    public String showMedical(Model model, HttpSession session) {
        Boolean userLoggedIn = (Boolean) session.getAttribute("userLoggedIn");
        List<Medical> medicalList = medicalRepository.findAll();
        double totalBudget = medicalList.stream()
                .mapToDouble(Medical::getAmount)
                .sum();

        model.addAttribute("medical", medicalList);
        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("userLoggedIn", userLoggedIn != null && userLoggedIn); // Add userLoggedIn attribute
        return "medical"; // Return view
    }

    @PostMapping("/medical/submit-all")
    public String submitAllMedical(Model model) {
        List<Medical> medicalList = medicalRepository.findAll();
        double totalBudget = medicalList.stream()
                .mapToDouble(Medical::getAmount)
                .sum();

        if (totalBudget > 1000000) {
            model.addAttribute("error", "Ditolak karena melebihi budget max 1,000,000");
            return "medical"; // Return to the same page with error message
        }

        // Save all medical entries to the dashboard
        for (Medical medical : medicalList) {
            Dashboard dashboard = new Dashboard();
            dashboard.setName(medical.getName());
            dashboard.setAmount(medical.getAmount());
            dashboard.setReason(medical.getReason());
            dashboard.setType("Medical");  // Set the type to "Medical"
            dashboardRepository.save(dashboard);
        }

        // Delete all the medical entries after saving them to the dashboard
        medicalRepository.deleteAll();

        // Add success message to model
        model.addAttribute("successMessage", "Data berhasil dikirim dan Data sudah dihapus");

        return "redirect:/dashboard"; // Redirect to the dashboard page after successful submission
    }


    @PostMapping("/medical/update-status")
    public String updateMedicalStatus(@RequestParam Long id) {
        Medical medical = medicalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        // Logic to set the status based on the amount
        if (medical.getAmount() > 1000000) {
            medical.setStatus("Ditolak Pengajuan");
        } else {
            medical.setStatus("Data Sesuai");
        }

        medicalRepository.save(medical);
        return "redirect:/medical";
    }

    // Edit Kantor (Only "User" can edit)
    @PostMapping("/medical/edit")
    public String editMedical(@RequestParam Long id, @RequestParam String name, @RequestParam double amount, @RequestParam String reason, HttpSession session, Model model) {
        if (!isUserLoggedIn(session)) {
            model.addAttribute("error", "Edit or delete cannot be used by other users.");
            List<Medical> medicalList = medicalRepository.findAll(); // Add the list of 'Medical' to the model
            model.addAttribute("medical", medicalList);
            return "medical"; // Redirect to the same page with error, but all tables are visible
        }

        Medical medical = medicalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        String loggedInUser = (String) session.getAttribute("username");
        // Ensure the logged-in user is the one who owns the bisnis entry
        if (!medical.getName().equals(loggedInUser)) {
            model.addAttribute("error", "Edit or delete cannot be used by other users.");
            List<Medical> medicalList = medicalRepository.findAll(); // Add the list of 'Bisnis' to the model
            model.addAttribute("medical", medicalList);
            return "medical"; // Redirect to the same page with error, but all tables are visible
        }

        medical.setName(name);
        medical.setAmount(amount);
        medical.setReason(reason);
        medicalRepository.save(medical);
        return "redirect:/medical";
    }

    // Delete Kantor (Only "User" can delete)
    @DeleteMapping("/medical/delete/{id}")
    @ResponseBody
    public String deleteMedical(@PathVariable Long id, HttpSession session, Model model) {
        if (!isUserLoggedIn(session)) {
            return "Edit or delete cannot be used by other users.";  // Prevent non-users from deleting
        }

        Medical medical = medicalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        String loggedInUser = (String) session.getAttribute("username");
        // Ensure the logged-in user is the one who owns the bisnis entry
        if (!medical.getName().equals(loggedInUser)) {
            return "Edit or delete cannot be used by other users.";  // Prevent other users from deleting
        }

        medicalRepository.deleteById(id);
        return "Deleted";
    }

    @GetMapping("/medical/{id}")
    @ResponseBody
    public Medical getMedicalById(@PathVariable Long id) {
        return medicalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
    }

//end Medical code

    // Logout method
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();

        // Prevent caching
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return "redirect:/login-admin";
    }

}
