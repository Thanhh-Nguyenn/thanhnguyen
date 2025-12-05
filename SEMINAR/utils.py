import sqlite3
import datetime
import re
from unidecode import unidecode

# Database
DB_NAME = 'sentiment_history.db'

def init_db():
    """Khởi tạo cơ sở dữ liệu và bảng history."""
    conn = sqlite3.connect(DB_NAME)
    c = conn.cursor()
    c.execute('''
        CREATE TABLE IF NOT EXISTS history (
            id INTEGER PRIMARY KEY,
            text_input TEXT NOT NULL,
            predicted_label TEXT NOT NULL,
            confidence_score REAL NOT NULL,
            timestamp TEXT NOT NULL
        )
    ''')
    conn.commit()
    conn.close()

def insert_result(text_input, label, score):
    """Lưu trữ kết quả phân loại."""
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    conn = sqlite3.connect(DB_NAME)
    c = conn.cursor()
    c.execute(
    "INSERT INTO history (text_input, predicted_label, confidence_score, timestamp) VALUES (?, ?, ?, ?)",
    # Dữ liệu được truyền riêng, ngăn SQL injection
    (text_input, label, score, timestamp) 
)
    conn.commit()
    conn.close()

def get_history(limit=10):
    """Truy vấn và trả về lịch sử phân loại."""
    conn = sqlite3.connect(DB_NAME)
    conn.row_factory = sqlite3.Row 
    c = conn.cursor()
    c.execute(f"SELECT * FROM history ORDER BY id DESC LIMIT {limit}")
    history_data = c.fetchall()
    conn.close()
    return [dict(row) for row in history_data]

def clear_history():
    """Xóa tất cả các bản ghi khỏi bảng history."""
    conn = sqlite3.connect(DB_NAME)
    c = conn.cursor()
    c.execute("DELETE FROM history")
    conn.commit()
    conn.close()

# --- Xử lý Tiền Xử Lý (Xử lý biến thể Tiếng Việt) ---
VIETNAMESE_CONTRACTIONS = {
    "ko": "không", "k": "không", "dc": "được", 
    "r": "rồi", "qá": "quá", "bt": "bình thường", 
    "sp": "sản phẩm", "nhìu": "nhiều", "m": "mày"
}

def preprocess_pipeline(raw_text):
    """Áp dụng tiền xử lý: chữ thường, xử lý viết tắt."""
    text = raw_text.lower()
    words = text.split()
    normalized_words = []
    
    for word in words:
        clean_word = re.sub(r'[^\w\s]', '', word)
        
        if clean_word in VIETNAMESE_CONTRACTIONS:
            normalized_words.append(VIETNAMESE_CONTRACTIONS[clean_word])
        else:
            normalized_words.append(word)

    text = " ".join(normalized_words)
    text = re.sub(r'[^a-z0-9áàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđ\s]', '', text)

    return text.strip()

init_db()
