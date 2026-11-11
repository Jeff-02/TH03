# WarehouseManager - Quản lý kho hàng (Java + XML + SQLite)

Ứng dụng Android hỗ trợ chủ cửa hàng nhỏ quản lý sản phẩm trong kho, cập nhật số lượng tồn, giá bán và cảnh báo sản phẩm sắp hết hàng.

## 1. Tài khoản mẫu
- **Tên đăng nhập:** `admin`
- **Mật khẩu:** `123456`

## 2. Hướng dẫn Build & Chạy ứng dụng

### Cách 1: Sử dụng Android Studio
1. Mở thư mục project `TH03` trong Android Studio.
2. Đợi Gradle Sync hoàn tất.
3. Chọn thiết bị ảo (Emulator) hoặc thiết bị thật (Android SDK >= 24).
4. Nhấn **Run 'app'** (`Shift + F10`) để build và chạy ứng dụng.

### Cách 2: Sử dụng dòng lệnh (Command Line)
1. Mở cửa sổ lệnh tại thư mục gốc của project.
2. Chạy lệnh build file APK:
   ```bash
   ./gradlew assembleDebug
   ```
   *(Hoặc `gradlew.bat assembleDebug` trên Windows)*
3. File APK sau khi build thành công sẽ nằm tại:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## 3. Cấu trúc thư mục mã nguồn

```
TH03/
├─ app/src/main/
│  ├─ AndroidManifest.xml
│  ├─ java/com/example/th03/
│  │  ├─ LoginActivity.java          # Màn hình đăng nhập
│  │  ├─ MainActivity.java           # Màn hình thống kê kho & danh sách sản phẩm
│  │  ├─ AddProductActivity.java     # Màn hình thêm sản phẩm mới
│  │  ├─ ProductDetailActivity.java  # Màn hình chi tiết, nhập/bán/xóa sản phẩm
│  │  ├─ Product.java                # Lớp đối tượng sản phẩm
│  │  ├─ DatabaseHelper.java         # Quản lý SQLite DB (warehouse.db)
│  │  ├─ ProductAdapter.java         # Adapter cho RecyclerView danh sách sản phẩm
│  │  └─ FormatUtils.java            # Tiện ích định dạng tiền tệ (150.000 đ)
│  └─ res/
│     ├─ layout/
│     │  ├─ activity_login.xml
│     │  ├─ activity_main.xml
│     │  ├─ activity_add_product.xml
│     │  ├─ activity_product_detail.xml
│     │  └─ item_product.xml
│     ├─ drawable/
│     │  ├─ bg_low_stock_badge.xml
│     │  ├─ bg_category_chip.xml
│     │  └─ bg_spinner_border.xml
│     └─ values/ strings.xml, colors.xml, themes.xml
└─ README.md
```

---

## 4. Chức năng chính
1. **Đăng nhập (`LoginActivity`):** Kiểm tra thông tin với bảng `users` trong SQLite (`admin/123456`). Báo lỗi khi sai hoặc bỏ trống.
2. **Danh sách & Thống kê (`MainActivity`):** Thống kê tổng số sản phẩm, tổng giá trị kho, số lượng sản phẩm sắp hết hàng (SL < 5). Hiển thị danh sách dạng card view với nhãn "Sắp hết hàng" màu đỏ cho sản phẩm tồn < 5.
3. **Thêm sản phẩm (`AddProductActivity`):** Form nhập thông tin đầy đủ, validate dữ liệu (tên, giá > 0, số lượng >= 0), lưu vào SQLite DB và cập nhật lại màn hình danh sách.
4. **Chi tiết & Thao tác (`ProductDetailActivity`):**
   - Xem chi tiết thông tin sản phẩm.
   - **Nhập thêm 1:** Tăng SL lên 1, ghi lại DB.
   - **Bán 1:** Giảm SL đi 1 (nếu SL = 0 báo "Hết hàng").
   - **Xóa sản phẩm:** Xóa khỏi DB và quay về danh sách.
