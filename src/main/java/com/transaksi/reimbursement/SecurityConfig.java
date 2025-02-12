//import org.springframework.web.bind.annotation.*;
//
//@GetMapping("/reimbursement")
//public String showReimbursementForm() {
//        return "reimbursement"; // Pastikan ada file reimbursement.html di direktori templates
//        }
//
////    @PostMapping("/reimbursement")
////    public String submitReimbursement(@RequestParam String name, @RequestParam double amount, @RequestParam String reason) {
////        // Membuat objek transaksi dari reimbursement
////        Transaction transaction = new Transaction();
////        transaction.setName(name);
////        transaction.setAmount(amount);
////        transaction.setReason(reason);
////
////        // Tentukan ID secara manual jika diperlukan
////        transaction.setId(System.currentTimeMillis()); // Ini hanya contoh, sesuaikan dengan kebutuhan Anda
////
////        transactionRepository.save(transaction);
////        return "redirect:/transactions";
////    }
//
//@PostMapping("/reimbursement")
//public String submitReimbursement(
//@RequestParam String name,
//@RequestParam double amount,
//@RequestParam String reason,
//@RequestParam String type) {
//
//        switch (type) {
//        case "Bisnis":
//        Bisnis bisnis = new Bisnis();
//        bisnis.setName(name);
//        bisnis.setAmount(amount);
//        bisnis.setReason(reason);
//        bisnisRepository.save(bisnis);
//        break;
//
//        case "Kantor":
//        Kantor kantor = new Kantor();
//        kantor.setId(System.currentTimeMillis()); // Memberikan ID manual
//        kantor.setName(name);
//        kantor.setAmount(amount);
//        kantor.setReason(reason);
//        kantorRepository.save(kantor);
//        break;
//
//        case "Medical":
//        Medical medical = new Medical();
//        medical.setId(System.currentTimeMillis()); // Memberikan ID manual
//        medical.setName(name);
//        medical.setAmount(amount);
//        medical.setReason(reason);
//        medicalRepository.save(medical);
//        break;
//
//default:
//        throw new IllegalArgumentException("Invalid reimbursement type: " + type);
//        }
//
//        return "redirect:/reimbursement";
//        }
//
////start dashboard code
//
//@GetMapping("/dashboard")
//public String showDashboard(Model model) {
//        List<Dashboard> dashboards = dashboardRepository.findAll();
//        double totalBudget = dashboards.stream()
//        .mapToDouble(Dashboard::getAmount)
//        .sum();
//        model.addAttribute("totalBudget", totalBudget);
//        model.addAttribute("dashboards", dashboards);
//        return "dashboard";  // Ensure that the model contains dashboards
//        }
//
//@PostMapping("/dashboard/update-status")
//@ResponseBody
//public Dashboard updateDashboardStatus(@RequestParam Long id) {
//        Dashboard dashboard = dashboardRepository.findById(id)
//        .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
//
//        // Logic to set the status based on the amount
//        String newStatus = "Menunggu Proses"; // Default status
//        if (dashboard.getAmount() > 1000000) {
//        newStatus = "Ditolak"; // Set to red (Ditolak) if amount exceeds limit
//        } else if (dashboard.getAmount() <= 1000000) {
//        newStatus = "Disetujui"; // Set to green (Disetujui) if within the budget
//        }
//
//        if (!newStatus.equals(dashboard.getStatus())) {  // Only update if status is different
//        dashboard.setStatus(newStatus);
//        dashboardRepository.save(dashboard);
//        }
//
//        return dashboard; // Return the updated dashboard object
//        }
//
//
//
//
//@PostMapping("/dashboard/edit")
//public String editDashboard(@RequestParam Long id, @RequestParam String name, @RequestParam double amount, @RequestParam String reason, @RequestParam String status) {
//        Dashboard dashboard = dashboardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
//        dashboard.setName(name);
//        dashboard.setAmount(amount);
//        dashboard.setReason(reason);
//        dashboard.setStatus(status);  // Update status here
//        dashboardRepository.save(dashboard);
//        return "redirect:/dashboard";
//        }
//
//@DeleteMapping("/dashboard/delete/{id}")
//@ResponseBody
//public String deleteDashboard(@PathVariable Long id) {
//        dashboardRepository.deleteById(id);
//        return "Deleted";
//        }
//
//@GetMapping("/dashboard/{id}")
//@ResponseBody
//public Dashboard getDashboardById(@PathVariable Long id) {
//        return dashboardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
//        }
//
////end Kantor code
//
////start bisnis code
//
//@GetMapping("/bisnis")
//public String showBisnis(Model model) {
//        List<Bisnis> bisnisList = bisnisRepository.findAll();
//        double totalBudget = bisnisList.stream()
//        .mapToDouble(Bisnis::getAmount)
//        .sum();
//
//        model.addAttribute("bisnis", bisnisList);
//        model.addAttribute("totalBudget", totalBudget);
//        return "bisnis"; // ensure this matches your template file name
//        }
//
//
//
//@PostMapping("/bisnis/update-status")
//public String updateBisnisStatus(@RequestParam Long id) {
//        Bisnis bisnis = bisnisRepository.findById(id)
//        .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
//
//        // Logic to set the status based on the amount
//        if (bisnis.getAmount() > 1000000) {
//        bisnis.setStatus("Ditolak Pengajuan");
//        } else {
//        bisnis.setStatus("Pembayaran Sesuai");
//        }
//
//        bisnisRepository.save(bisnis);
//        return "redirect:/bisnis";
//        }
//
//@PostMapping("/bisnis/submit-all")
//public String submitAllBisnis(Model model) {
//        List<Bisnis> bisnisList = bisnisRepository.findAll();
//        double totalBudget = bisnisList.stream()
//        .mapToDouble(Bisnis::getAmount)
//        .sum();
//
//        if (totalBudget > 1000000) {
//        model.addAttribute("error", "Ditolak karena melebihi budget max 1,000,000");
//        return "redirect:/bisnis?error=true";
//        }
//
//        for (Bisnis bisnis : bisnisList) {
//        Dashboard dashboard = new Dashboard();
//        dashboard.setName(bisnis.getName());
//        dashboard.setAmount(bisnis.getAmount());
//        dashboard.setReason(bisnis.getReason());
//        dashboardRepository.save(dashboard);  // Save data to Dashboard
//        }
//
//        return "redirect:/dashboard";
//        }
//
//
//
//@PostMapping("/bisnis/edit")
//public String editBisnis(@RequestParam Long id, @RequestParam String name, @RequestParam double amount, @RequestParam String reason) {
//        Bisnis bisnis = bisnisRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
//        bisnis.setName(name);
//        bisnis.setAmount(amount);
//        bisnis.setReason(reason);
//        bisnisRepository.save(bisnis);
//        return "redirect:/bisnis";
//        }
//
//@DeleteMapping("/bisnis/delete/{id}")
//@ResponseBody
//public String deleteBisnis(@PathVariable Long id) {
//        bisnisRepository.deleteById(id);
//        return "Deleted";
//        }
//
//@GetMapping("/bisnis/{id}")
//@ResponseBody
//public Bisnis getBisnisById(@PathVariable Long id) {
//        return bisnisRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
//        }
//
////end Bisnis code
//
////start Kantor code
//
//@GetMapping("/kantor")
//public String showRiwayat(Model model) {
//        List<Kantor> kantors = kantorRepository.findAll();
//        model.addAttribute("kantors", kantors);
//        return "kantor"; // Sesuaikan dengan subdirektori
//        }
//
//
//@PostMapping("/kantor/edit")
//public String editKantor(@RequestParam Long id, @RequestParam String name, @RequestParam double amount, @RequestParam String reason) {
//        Kantor kantor = kantorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
//        kantor.setName(name);
//        kantor.setAmount(amount);
//        kantor.setReason(reason);
//        kantorRepository.save(kantor);
//        return "redirect:/kantor";
//        }
//
//@DeleteMapping("/kantor/delete/{id}")
//@ResponseBody
//public String deleteKantor(@PathVariable Long id) {
//        kantorRepository.deleteById(id);
//        return "Deleted";
//        }
//
//@GetMapping("/kantor/{id}")
//@ResponseBody
//public Kantor getKantorById(@PathVariable Long id) {
//        return kantorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
//        }
//
////end Kantor code
//
////start Medical code
//
//@GetMapping("/medical")
//public String showMedical(Model model) {
//        List<Medical> medicals = medicalRepository.findAll();
//        model.addAttribute("medicals", medicals);
//        return "medical"; // Sesuaikan dengan subdirektori
//        }
//
//@PostMapping("/medical/edit")
//public String editMedical(@RequestParam Long id, @RequestParam String name, @RequestParam double amount, @RequestParam String reason) {
//        Medical medical = medicalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
//        medical.setName(name);
//        medical.setAmount(amount);
//        medical.setReason(reason);
//        medicalRepository.save(medical);
//        return "redirect:/medical";
//        }
//
//@DeleteMapping("/medical/delete/{id}")
//@ResponseBody
//public String deleteMedical(@PathVariable Long id) {
//        medicalRepository.deleteById(id);
//        return "Deleted";
//        }
//
//@GetMapping("/medical/{id}")
//@ResponseBody
//public Medical getMedicalById(@PathVariable Long id) {
//        return medicalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
//        }
//
////end Medical code