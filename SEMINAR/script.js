document.addEventListener('DOMContentLoaded', () => {
    const classifyBtn = document.getElementById('classify_btn');
    const textInput = document.getElementById('text_input');
    const clearHistoryBtn = document.getElementById('clear_history_btn'); 
    const resultBox = document.getElementById('result_box');
    const resultLabel = document.getElementById('result_label');
    const resultScore = document.getElementById('result_score');
    const messageBox = document.getElementById('message_box');
    const historyTableBody = document.querySelector('#history_table tbody');

    // Hàm tải lại lịch sử (Được gọi sau khi phân loại thành công/xóa)
    const fetchHistory = async () => {
        try {
            const response = await fetch('/api/history');
            const history = await response.json();

            historyTableBody.innerHTML = ''; 

            if (history.length === 0) {
                historyTableBody.innerHTML = '<tr><td colspan="4">Chưa có lịch sử phân loại nào.</td></tr>';
                return;
            }

            history.forEach(item => {
                const row = historyTableBody.insertRow();
                row.insertCell().textContent = item.text_input; 
                
                const labelCell = row.insertCell();
                labelCell.textContent = item.predicted_label;
                labelCell.className = `label-${item.predicted_label.toLowerCase()}`;
                
                row.insertCell().textContent = item.confidence_score;
                row.insertCell().textContent = item.timestamp;
            });

        } catch (error) {
            console.error('Error fetching history:', error);
        }
    };
    
    // Hàm gọi API phân loại
    const classifyText = async () => {
        const text = textInput.value.trim();
        
        // Kiểm tra input (>= 5 ký tự) ngay trên Frontend
        if (text === "" || text.length < 5) {
            const error_message = "Câu không hợp lệ, thử lại. Vui lòng nhập tối thiểu 5 ký tự.";
            messageBox.textContent = error_message;
            messageBox.style.color = 'red';
            alert(error_message); // Hiển thị pop-up
            return;
        }

        resultBox.classList.add('hidden');
        messageBox.textContent = "Đang phân tích...";
        messageBox.style.color = '#007bff';
        classifyBtn.disabled = true;

        try {
            const response = await fetch('/api/classify', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ text: text })
            });

            const data = await response.json();

            if (response.ok) {
                // Thành công
                resultLabel.textContent = data.label;
                resultScore.textContent = data.score;
                resultLabel.className = `label-${data.label.toLowerCase()}`;

                resultBox.classList.remove('hidden');
                messageBox.textContent = data.message;
                messageBox.style.color = 'green';
                
                await fetchHistory(); 
                
            } else {
                // Xử lý lỗi từ server (400, 500)
                messageBox.textContent = data.message;
                messageBox.style.color = 'red';
                alert(data.message); 
            }
        } catch (error) {
            messageBox.textContent = 'Lỗi kết nối đến server.';
            messageBox.style.color = 'red';
            console.error('Fetch error:', error);
            alert('Lỗi kết nối đến server.'); 
        } finally {
            classifyBtn.disabled = false;
        }
    };

    // HÀM XÓA LỊCH SỬ
    const clearHistory = async () => {
        if (!confirm("Bạn có chắc chắn muốn xóa toàn bộ lịch sử phân loại? Hành động này không thể hoàn tác.")) {
            return;
        }
        
        messageBox.textContent = "Đang xóa lịch sử...";
        messageBox.style.color = '#007bff';

        try {
            const response = await fetch('/api/clear_history', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
            });

            const data = await response.json();

            if (response.ok) {
                messageBox.textContent = data.message;
                messageBox.style.color = 'green';
                
                await fetchHistory(); 
            } else {
                messageBox.textContent = `Lỗi: ${data.message}`;
                messageBox.style.color = 'red';
            }
        } catch (error) {
            messageBox.textContent = 'Lỗi kết nối server khi xóa lịch sử.';
            messageBox.style.color = 'red';
            console.error('Clear history error:', error);
        }
    };
    
    classifyBtn.addEventListener('click', classifyText);
    clearHistoryBtn.addEventListener('click', clearHistory);
    
});
