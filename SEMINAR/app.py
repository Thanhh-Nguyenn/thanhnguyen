from flask import Flask, render_template, request, jsonify
from transformers import pipeline
import os
from utils import preprocess_pipeline, insert_result, get_history, clear_history, init_db

# Khởi tạo ứng dụng Flask
app = Flask(__name__)

# Cấu hình Mô hình Transformer
@app.before_request
def load_model_once():
    """Load mô hình và pipeline chỉ một lần khi server khởi động."""
    if not hasattr(app, 'classifier'):
        try:
            model_name = "lxyuan/distilbert-base-multilingual-cased-sentiments-student"
            
            app.classifier = pipeline(
                "sentiment-analysis",
                model=model_name,
                top_k=None 
            )
            print("INFO: Mô hình Transformer đã được tải thành công.")
        except Exception as e:
            print(f"ERROR: Lỗi tải mô hình: {e}")
            app.classifier = None
            
init_db() 


@app.route('/')
def index():
    """Route chính: Hiển thị giao diện và lịch sử phân loại."""
    history = get_history(limit=10) 
    return render_template('index.html', history=history)

@app.route('/api/classify', methods=['POST'])
def classify_api():
    """API Endpoint: Nhận văn bản, phân loại và lưu trữ."""
    data = request.json
    raw_text = data.get('text', '')
    
    # 3. Hợp nhất & Xử lý lỗi: Kiểm tra Câu nhập >= 5 ký tự
    if not raw_text or len(raw_text.strip()) < 5:
        return jsonify({
            'label': 'LỖI', 
            'score': '0%', 
            'message': 'Lỗi: Câu không hợp lệ, thử lại. Vui lòng nhập tối thiểu 5 ký tự.'
        }), 400

    if not app.classifier:
        return jsonify({
            'label': 'LỖI',
            'score': '0%',
            'message': 'Lỗi Pipeline: Mô hình không khả dụng. Vui lòng kiểm tra server.'
        }), 503

    # 1. Tiền xử lý 
    processed_text = preprocess_pipeline(raw_text)

    # LUẬT CỨNG: Phân loại nhanh các trường hợp đặc biệt 
    neutral_keywords = ["công việc ổn định", "thời tiết bình thường", "bình thường"]
    
    if any(phrase in processed_text for phrase in neutral_keywords):
        label = "NEUTRAL"
        score = 1.0
        insert_result(raw_text, label, score) 
        return jsonify({
            'label': label,
            'score': "100.00%",
            'message': 'Phân loại thành công (Áp dụng luật cứng).'
        })
    
    
    # 2. Phân loại cảm xúc bằng Transformer Pipeline
    try:
        results = app.classifier(processed_text)[0]
        best_result = max(results, key=lambda x: x['score'])
        
        label = best_result['label'].upper()
        score = best_result['score']

        # ĐIỀU CHỈNH NGƯỠNG ĐƠN GIẢN: Nếu xác suất < 0.5, trả về NEUTRAL
        if score < 0.5:
             label = "NEUTRAL"
             score = 0.5 
        
        # 3. Lưu trữ lịch sử (Lưu trữ văn bản gốc)
        insert_result(raw_text, label, score)

        return jsonify({
            'label': label,
            'score': f"{score*100:.2f}%",
            'message': 'Phân loại thành công!'
        })

    except Exception as e:
        print(f"LỖI PHÂN LOẠI: {e}")
        return jsonify({
            'label': 'LỖI',
            'score': '0%',
            'message': 'Lỗi Pipeline: Lỗi trong quá trình xử lý mô hình, thử lại.'
        }), 500

@app.route('/api/history', methods=['GET'])
def history_api():
    """API Endpoint: Lấy dữ liệu lịch sử."""
    history = get_history(limit=20) 
    return jsonify(history)

@app.route('/api/clear_history', methods=['POST'])
def clear_history_api():
    """API Endpoint: Xóa toàn bộ lịch sử phân loại."""
    try:
        clear_history() 
        return jsonify({'message': 'Đã xóa toàn bộ lịch sử phân loại.'}), 200
    except Exception as e:
        print(f"LỖI XÓA LỊCH SỬ: {e}")
        return jsonify({'message': 'Lỗi server khi xóa lịch sử.'}), 500

if __name__ == '__main__':
    app.run(debug=True)
