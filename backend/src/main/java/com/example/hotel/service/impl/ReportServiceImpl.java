package com.example.hotel.service.impl;

import com.example.hotel.entity.Guest;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.RoomType;
import com.example.hotel.service.DashboardService;
import com.example.hotel.service.GuestService;
import com.example.hotel.service.ReportService;
import com.example.hotel.service.ReservationService;
import com.example.hotel.service.RoomService;
import com.example.hotel.service.RoomTypeService;
import com.example.hotel.vo.DashboardTrendPointVO;
import com.example.hotel.vo.DashboardTrendVO;
import com.example.hotel.vo.ReservationVO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryDataSource;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {

    private final DashboardService dashboardService;
    private final RoomTypeService roomTypeService;
    private final RoomService roomService;
    private final ReservationService reservationService;
    private final GuestService guestService;

    public ReportServiceImpl(DashboardService dashboardService,
                             RoomTypeService roomTypeService,
                             RoomService roomService,
                             ReservationService reservationService,
                             GuestService guestService) {
        this.dashboardService = dashboardService;
        this.roomTypeService = roomTypeService;
        this.roomService = roomService;
        this.reservationService = reservationService;
        this.guestService = guestService;
    }

    @Override
    public byte[] exportOperationsReport() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            buildOverviewSheet(workbook.createSheet("Overview"));
            buildTrendSheet(workbook.createSheet("Trends"));
            buildRevenueSheet(workbook.createSheet("RevenueBreakdown"));
            buildRoomTypeSheet(workbook.createSheet("RoomTypes"));
            buildRoomSheet(workbook.createSheet("Rooms"));
            buildReservationSheet(workbook.createSheet("Reservations"));
            buildGuestSheet(workbook.createSheet("Guests"));
            autosizeAllSheets(workbook, 10);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("failed to export excel report", ex);
        }
    }

    private void buildOverviewSheet(Sheet sheet) {
        var overview = dashboardService.getOverview();
        applyHeader(writeRow(sheet, 0, "Metric", "Value"));
        writeRow(sheet, 1, "Available Rooms", overview.getAvailableRooms());
        writeRow(sheet, 2, "In House Reservations", overview.getInHouseReservations());
        writeRow(sheet, 3, "Today Arrivals", overview.getTodayArrivals());
        writeRow(sheet, 4, "Last 30 Days Revenue", overview.getLastThirtyDaysRevenue());
    }

    private void buildTrendSheet(Sheet sheet) {
        DashboardTrendVO trends = dashboardService.getTrends();
        applyHeader(writeRow(sheet, 0, "Date", "Revenue", "Arrivals", "Departures", "Booked"));
        int rowIndex = 1;
        for (DashboardTrendPointVO point : trends.points()) {
            writeRow(sheet, rowIndex++, point.date(), point.revenue(), point.arrivals(), point.departures(), point.bookedCount());
        }
        createTrendChart(sheet, trends.points().size());
    }

    private void buildRevenueSheet(Sheet sheet) {
        List<ReservationVO> reservations = reservationService.listReservationDetails();
        applyHeader(writeRow(sheet, 0, "ReservationNo", "RoomFee", "Breakfast", "ExtraBed", "Deposit", "Coupon", "FinalTotal"));
        int rowIndex = 1;
        for (ReservationVO reservation : reservations) {
            writeRow(sheet, rowIndex++, reservation.getReservationNo(), reservation.getRoomFee(), reservation.getBreakfastFee(),
                    reservation.getExtraBedFee(), reservation.getDepositAmount(), reservation.getCouponAmount(), reservation.getTotalAmount());
        }
        createRevenueBarChart(sheet, reservations.size());
    }

    private void buildRoomTypeSheet(Sheet sheet) {
        List<RoomType> roomTypes = roomTypeService.listAll();
        applyHeader(writeRow(sheet, 0, "ID", "Name", "Price", "MaxGuests", "BedType", "Area"));
        int rowIndex = 1;
        for (RoomType roomType : roomTypes) {
            writeRow(sheet, rowIndex++, roomType.getId(), roomType.getName(), roomType.getBasePrice(),
                    roomType.getMaxGuests(), roomType.getBedType(), roomType.getArea());
        }
    }

    private void buildRoomSheet(Sheet sheet) {
        List<Room> rooms = roomService.listAllRooms();
        applyHeader(writeRow(sheet, 0, "ID", "RoomNumber", "RoomTypeId", "Floor", "Status", "CleanStatus"));
        int rowIndex = 1;
        for (Room room : rooms) {
            writeRow(sheet, rowIndex++, room.getId(), room.getRoomNumber(), room.getRoomTypeId(),
                    room.getFloor(), room.getStatus(), room.getCleanStatus());
        }
    }

    private void buildReservationSheet(Sheet sheet) {
        List<ReservationVO> reservations = reservationService.listReservationDetails();
        applyHeader(writeRow(sheet, 0, "ReservationNo", "Guest", "Room", "CheckIn", "CheckOut", "RoomFee", "Breakfast", "ExtraBed", "Deposit", "Coupon", "Amount", "Status", "Channel"));
        int rowIndex = 1;
        for (ReservationVO reservation : reservations) {
            writeRow(sheet, rowIndex++, reservation.getReservationNo(), reservation.getGuestName(), reservation.getRoomNumber(),
                    reservation.getCheckInDate(), reservation.getCheckOutDate(), reservation.getRoomFee(),
                    reservation.getBreakfastFee(), reservation.getExtraBedFee(), reservation.getDepositAmount(),
                    reservation.getCouponAmount(), reservation.getTotalAmount(),
                    reservation.getStatus(), reservation.getChannel());
        }
    }

    private void buildGuestSheet(Sheet sheet) {
        List<Guest> guests = guestService.list();
        applyHeader(writeRow(sheet, 0, "ID", "Name", "Phone", "MemberLevel", "TotalReservations", "TotalSpent", "Remark"));
        int rowIndex = 1;
        for (Guest guest : guests) {
            var profile = guestService.getProfile(guest.getId());
            writeRow(sheet, rowIndex++, guest.getId(), guest.getFullName(), guest.getPhone(),
                    guest.getMemberLevel(), profile.stats().totalReservations(), profile.stats().totalSpent(), guest.getRemark());
        }
    }

    private Row writeRow(Sheet sheet, int rowIndex, Object... values) {
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            Object value = values[i];
            if (value == null) {
                cell.setBlank();
            } else if (value instanceof Number number) {
                cell.setCellValue(number.doubleValue());
            } else {
                cell.setCellValue(String.valueOf(value));
            }
        }
        return row;
    }

    private void applyHeader(Row row) {
        CellStyle style = row.getSheet().getWorkbook().createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        row.forEach(cell -> cell.setCellStyle(style));
    }

    private void autosizeAllSheets(Workbook workbook, int columns) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            for (int j = 0; j < columns; j++) {
                workbook.getSheetAt(i).autoSizeColumn(j);
            }
        }
    }

    private void createTrendChart(Sheet sheet, int size) {
        if (size <= 0 || !(sheet instanceof XSSFSheet xssfSheet)) {
            return;
        }
        XSSFDrawing drawing = xssfSheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 6, 1, 14, 18);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Revenue Trend");
        chart.getOrAddLegend().setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        XDDFDataSource<String> dates = XDDFDataSourcesFactory.fromStringCellRange(xssfSheet, new CellRangeAddress(1, size, 0, 0));
        XDDFNumericalDataSource<Double> revenue = XDDFDataSourcesFactory.fromNumericCellRange(xssfSheet, new CellRangeAddress(1, size, 1, 1));

        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        XDDFLineChartData.Series series = (XDDFLineChartData.Series) data.addSeries(dates, revenue);
        series.setTitle("Revenue", null);
        chart.plot(data);
    }

    private void createRevenueBarChart(Sheet sheet, int size) {
        if (size <= 0 || !(sheet instanceof XSSFSheet xssfSheet)) {
            return;
        }
        XSSFDrawing drawing = xssfSheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 8, 1, 16, 18);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Reservation Revenue Breakdown");
        chart.getOrAddLegend().setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        XDDFDataSource<String> reservationNos = XDDFDataSourcesFactory.fromStringCellRange(xssfSheet, new CellRangeAddress(1, size, 0, 0));
        XDDFNumericalDataSource<Double> roomFee = XDDFDataSourcesFactory.fromNumericCellRange(xssfSheet, new CellRangeAddress(1, size, 1, 1));
        XDDFNumericalDataSource<Double> breakfast = XDDFDataSourcesFactory.fromNumericCellRange(xssfSheet, new CellRangeAddress(1, size, 2, 2));
        XDDFNumericalDataSource<Double> extraBed = XDDFDataSourcesFactory.fromNumericCellRange(xssfSheet, new CellRangeAddress(1, size, 3, 3));

        XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        data.setBarDirection(BarDirection.COL);
        addBarSeries(data, reservationNos, roomFee, "Room Fee");
        addBarSeries(data, reservationNos, breakfast, "Breakfast");
        addBarSeries(data, reservationNos, extraBed, "Extra Bed");
        chart.plot(data);
    }

    private void addBarSeries(XDDFBarChartData data,
                              XDDFDataSource<String> categories,
                              XDDFNumericalDataSource<Double> values,
                              String title) {
        XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(categories, values);
        series.setTitle(title, null);
    }
}
