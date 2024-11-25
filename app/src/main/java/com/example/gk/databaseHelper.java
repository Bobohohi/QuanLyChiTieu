    package com.example.gk;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.util.Log;

    import androidx.annotation.Nullable;

    import java.util.ArrayList;
    import java.util.List;

    public class databaseHelper extends SQLiteOpenHelper {
        private static final String dbname = "QLChiTieu.db";
        private static final int version = 1;


        private static final String TABLE_CATEGORIES = "Categories";
        private static final String COLUMN_NAME = "name";
        private static final String COLUMN_TYPE = "type";


        public databaseHelper(Context context) {
            super(context, dbname, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Tạo bảng Users
            String createUsersTable = "CREATE TABLE Users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "email TEXT NOT NULL UNIQUE," +
                    "password_hash TEXT NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "last_login TIMESTAMP)";
            db.execSQL(createUsersTable);

            // Tạo bảng Categories
            String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    "category_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT NOT NULL," +
                    COLUMN_TYPE + " TEXT NOT NULL CHECK(" + COLUMN_TYPE + " IN ('Chi tiêu', 'Thu nhập'))," +
                    "user_id INTEGER," +
                    "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE)";
            db.execSQL(createCategoriesTable);

            // Tạo bảng Transactions
            String createTransactionsTable = "CREATE TABLE Transactions (" +
                    "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "amount REAL NOT NULL," +
                    "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "description TEXT," +
                    "category_id INTEGER," +
                    "user_id INTEGER," +
                    "FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE)";
            db.execSQL(createTransactionsTable);

            // Tạo bảng Budgets
            String createBudgetsTable = "CREATE TABLE Budgets (" +
                    "budget_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "category_id INTEGER," +
                    "amount REAL NOT NULL," +
                    "start_date TIMESTAMP," +
                    "end_date TIMESTAMP," +
                    "user_id INTEGER," +
                    "FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE)";
            db.execSQL(createBudgetsTable);

            // Tạo bảng Notifications
            String createNotificationsTable = "CREATE TABLE Notifications (" +
                    "notification_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "message TEXT NOT NULL," +
                    "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "status TEXT CHECK(status IN ('Đã đọc', 'Chưa đọc'))," +
                    "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE)";
            db.execSQL(createNotificationsTable);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Xóa các bảng cũ nếu chúng tồn tại
            db.execSQL("DROP TABLE IF EXISTS Users");
            db.execSQL("DROP TABLE IF EXISTS Categories");
            db.execSQL("DROP TABLE IF EXISTS Transactions");
            db.execSQL("DROP TABLE IF EXISTS Budgets");
            db.execSQL("DROP TABLE IF EXISTS Notifications");
            // Tạo lại bảng
            onCreate(db);
        }
        public int getUserId(String username) {
            int userId = -1;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT user_id FROM Users WHERE username = ?", new String[]{username});

            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0); // Lấy cột đầu tiên (user_id)
            }
            cursor.close();
            db.close();
            return userId; // Trả về user_id (hoặc -1 nếu không tìm thấy)
        }
        public int getCategoryIdByName(String categoryName) {
            int categoryId = -1; // Giá trị mặc định nếu không tìm thấy
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT category_id FROM Categories WHERE name = ?", new String[]{categoryName});

            if (cursor.moveToFirst()) {
                categoryId = cursor.getInt(0); // Lấy giá trị category_id từ cột đầu tiên
            }
            cursor.close();
            db.close();
            return categoryId; // Trả về category_id hoặc -1 nếu không tìm thấy
        }

        // Thêm người dùng mới
        public boolean addUser(String username, String email, String password) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("email", email);
            values.put("password_hash", password); // Mã hóa mật khẩu trước khi lưu
            long result = db.insert("Users", null, values);
            db.close();
            return result != -1; // Trả về true nếu thêm thành công
        }

        // Kiểm tra thông tin đăng nhập
        public boolean checkUserCredentials(String username, String password) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query("Users", null, "username=? AND password_hash=?", new String[]{username, password}, null, null, null);
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            return exists; // Trả về true nếu thông tin đăng nhập hợp lệ
        }

        public List<String> getCategoriesByType(String type) {
            List<String> categories = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();

            String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_CATEGORIES +
                    " WHERE " + COLUMN_TYPE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{type});

            if (cursor.moveToFirst()) {
                do {
                    categories.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();

            return categories;
        }

        // Thêm một danh mục mới
        public boolean addCategory(String name, String type, int userId) {
            if (userId == -1) {
                Log.e("DatabaseHelper", "UserId không hợp lệ");
                return false;
            }

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_TYPE, type);
            values.put("user_id", userId);

            long result = db.insert(TABLE_CATEGORIES, null, values);
            db.close();
            return result != -1; // Trả về true nếu thêm thành công
        }

        // Xóa một danh mục dựa trên category_id
            public boolean deleteCategory(int categoryId) {
                SQLiteDatabase db = this.getWritableDatabase();
                int result = db.delete(TABLE_CATEGORIES, "category_id=?", new String[]{String.valueOf(categoryId)});
                db.close();
                return result > 0; // Trả về true nếu xóa thành công
            }

        // Cập nhật tên và loại của một danh mục dựa trên category_id
        public boolean updateCategory(int categoryId, String newName, String newType) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, newName);
            values.put(COLUMN_TYPE, newType);

            int result = db.update(TABLE_CATEGORIES, values, "category_id=?", new String[]{String.valueOf(categoryId)});
            db.close();
            return result > 0; // Trả về true nếu cập nhật thành công
        }

        public boolean addTransaction(double amount, String date, String time, String description, int categoryId, int userId) {
            // Kiểm tra categoryId và userId trước khi thêm
            if (categoryId == -1 || userId == -1) {
                Log.e("DatabaseHelper", "CategoryId hoặc UserId không hợp lệ");
                return false;
            }

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("amount", amount);
            values.put("date", date + " " + time); // Kết hợp ngày và giờ
            values.put("description", description);
            values.put("category_id", categoryId);
            values.put("user_id", userId);

            long result = db.insert("Transactions", null, values);
            db.close();

            if (result == -1) {
                Log.e("DatabaseHelper", "Không thể thêm giao dịch");
                return false;
            }
            return true;
        }


        public List<String> getTransactionsByType(String type) {
            List<String> transactions = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();

            String query = "SELECT t.amount, t.date, t.description, c.name " +
                    "FROM Transactions t " +
                    "JOIN Categories c ON t.category_id = c.category_id " +
                    "WHERE c.type = ?";

            Cursor cursor = db.rawQuery(query, new String[]{type});

            if (cursor.moveToFirst()) {
                do {
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    transactions.add("Danh mục: " + categoryName + " | Số tiền: " + amount + " | Ngày: " + date + " | Mô tả: " + description);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();

            return transactions;
        }
        public String getCategoryTypeById(int categoryId) {
            String categoryType = null;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = null;

            try {
                String query = "SELECT type FROM categories WHERE id = ?";
                cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});

                if (cursor != null && cursor.moveToFirst()) {
                    categoryType = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }

            return categoryType;
        }
        public double getTotalIncomeFromTransactions() {
            double totalIncome = 0;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = null;

            try {
                // Cập nhật truy vấn với tên cột chính xác
                String query = "SELECT SUM(t.amount) as totalAmount FROM transactions t " +
                        "JOIN categories c ON t.category_id = c.category_id " +
                        "WHERE c.type = 'Thu nhập'";
                cursor = db.rawQuery(query, null);

                if (cursor != null && cursor.moveToFirst()) {
                    totalIncome = cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount"));
                }
            } catch (Exception e) {
                e.printStackTrace(); // Có thể thêm ghi log chi tiết hơn tại đây
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }

            return totalIncome;
        }
        public double getTotalExpensesFromTransactions() {
            double totalExpenses = 0;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = null;

            try {
                String query = "SELECT SUM(t.amount) as totalAmount FROM transactions t " +
                        "JOIN categories c ON t.category_id = c.category_id " +
                        "WHERE c.type = 'Chi tiêu'";
                cursor = db.rawQuery(query, null);

                if (cursor != null && cursor.moveToFirst()) {
                    totalExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }

            return totalExpenses;
        }
        public List<String> getCategoryNamesWithTypeChiTieu() {
            List<String> categoryNames = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();

            // Sửa tên bảng và chắc chắn rằng type 'Chi tiêu' đúng với dữ liệu bạn đang lưu
            Cursor cursor = db.rawQuery("SELECT name FROM Categories WHERE type = 'Chi tiêu'", null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    categoryNames.add(cursor.getString(cursor.getColumnIndexOrThrow("name"))); // Lấy tên danh mục từ cột 'name'
                }
                cursor.close();
            }
            db.close();
            return categoryNames;
        }
        public boolean addBudget(int categoryId, double amount, int userId) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("category_id", categoryId);
            values.put("amount", amount);
            values.put("start_date", System.currentTimeMillis());  // Sử dụng thời gian hiện tại
            values.put("end_date", System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);  // Thời gian hết hạn là 7 ngày sau (chỉ là ví dụ)
            values.put("user_id", userId);

            long result = db.insert("Budgets", null, values);
            db.close();
            return result != -1; // Trả về true nếu lưu thành công
        }
        public boolean addNotification(int userId, String message) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("message", message);
            values.put("status", "Chưa đọc");  // Default status as unread

            long result = db.insert("Notifications", null, values);
            db.close();
            return result != -1;  // True if insertion is successful
        }
    }
