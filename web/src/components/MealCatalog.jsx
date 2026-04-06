import React, { useState, useEffect } from 'react';

const MealCatalog = () => {
  const [meals, setMeals] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // MODAL & FORM STATE
  const [showModal, setShowModal] = useState(false);
  const [textData, setTextData] = useState({
    name: '',
    category: 'HIGH_PROTEIN',
    description: ''
  });
  const [selectedFile, setSelectedFile] = useState(null); 
  const [imagePreview, setImagePreview] = useState(null);

  useEffect(() => {
    fetchMeals();
  }, []);

  const fetchMeals = async () => {
    try {
      const token = localStorage.getItem('accessToken'); 
      const response = await fetch('http://localhost:8080/api/v1/admin/meals', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const result = await response.json();
      if (result.success) {
        setMeals(result.data);
      }
    } catch (error) {
      console.error("Failed to fetch meals:", error);
    } finally {
      setLoading(false);
    }
  };

  // --- UPDATED: Use textData instead of formData ---
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setTextData({ ...textData, [name]: value });
  };

  // --- BROUGHT BACK: Handle File Selection for Upload & Preview ---
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const resetForm = () => {
    setShowModal(false);
    setTextData({ name: '', category: 'HIGH_PROTEIN', description: '' });
    setSelectedFile(null);
    setImagePreview(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const formDataToSend = new FormData();
      formDataToSend.append('name', textData.name);
      formDataToSend.append('category', textData.category);
      formDataToSend.append('description', textData.description);
      
      if (selectedFile) {
        formDataToSend.append('imageFile', selectedFile);
      }

      const token = localStorage.getItem('accessToken');
      
      // --- THE SMART LOGIC ---
      // If editingMealId exists, we use PUT and add the ID to the URL.
      // Otherwise, we use POST to create a new one.
      const url = editingMealId 
        ? `http://localhost:8080/api/v1/admin/meals/${editingMealId}`
        : 'http://localhost:8080/api/v1/admin/meals';

      const method = editingMealId ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method: method,
        headers: { 'Authorization': `Bearer ${token}` },
        body: formDataToSend, 
      });
      
      const result = await response.json();
      
      if (result.success) {
        if (editingMealId) {
          // UPDATE UI: Replace the old meal in the list with the updated one
          setMeals(meals.map(m => m.id === editingMealId ? result.data : m));
        } else {
          // ADD UI: Just push the new meal to the end of the list
          setMeals([...meals, result.data]);
        }
        
        resetForm(); // Closes modal and clears state
        setEditingMealId(null); // Reset the editing tracker
      } else {
        alert("Action failed: " + result.message);
      }
    } catch (error) {
      console.error("Submit failed:", error);
    }
  };

  // DELETE LOGIC
  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this meal?")) return;

    try {
      const token = localStorage.getItem('accessToken');
      const response = await fetch(`http://localhost:8080/api/v1/admin/meals/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });

      // --- NEW: Check if the response is actually okay (200-299) ---
      if (!response.ok) {
        const errorText = await response.text(); // Read as text instead of JSON if it fails
        throw new Error(`Server returned ${response.status}: ${errorText}`);
      }

      const result = await response.json();
      if (result.success) {
        setMeals(meals.filter(m => m.id !== id));
      }
    } catch (error) {
      console.error("Delete failed:", error);
      alert("Delete failed: " + error.message);
    }
  };

  // EDIT LOGIC (Pre-fills the modal)
  const [editingMealId, setEditingMealId] = useState(null);

  const handleEditClick = (meal) => {
    setEditingMealId(meal.id);
    setTextData({
      name: meal.name,
      category: meal.category,
      description: meal.description
    });
    setImagePreview(meal.imageUrl);
    setShowModal(true);
  };

  const styles = {
    // ... layout styles ...
    pageContainer: { minHeight: '100%', padding: '0', backgroundColor: 'transparent', color: '#FFFFFF', fontFamily: 'sans-serif' },
    headerRow: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' },
    addButton: { backgroundColor: '#00FF66', color: '#121212', border: 'none', padding: '10px 20px', fontSize: '14px', fontWeight: 'bold', borderRadius: '6px', cursor: 'pointer' },
    catalogContainer: { backgroundColor: 'transparent', padding: '0' },
    grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '1.5rem' },
    card: { backgroundColor: '#1E1E1E', border: '1px solid #333', borderRadius: '8px', padding: '1rem' , minHeight: '380px', overflow: 'hidden'},
    image: { width: '100%', height: '180px', objectFit: 'cover', borderRadius: '6px', marginBottom: '1rem', backgroundColor: '#121212' },
    categoryTag: { backgroundColor: '#333', color: '#A0AEC0', padding: '4px 10px', borderRadius: '20px', fontSize: '11px', fontWeight: 'bold', display: 'inline-block', marginBottom: '8px', textTransform: 'uppercase' },
    
    // ... modal styles ...
    modalOverlay: { position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0, 0, 0, 0.8)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 },
    modalContent: { backgroundColor: '#1E1E1E', padding: '2rem', borderRadius: '12px', width: '500px', maxHeight: '90vh', overflowY: 'auto', border: '1px solid #333' },
    formGroup: { marginBottom: '1rem', display: 'flex', flexDirection: 'column' },
    label: { marginBottom: '0.5rem', color: '#A0AEC0', fontSize: '14px' },
    input: { backgroundColor: '#121212', border: '1px solid #333', color: '#FFF', padding: '10px', borderRadius: '6px', fontSize: '14px' },
    imagePreview: { width: '100px', height: '100px', objectFit: 'cover', borderRadius: '8px', marginTop: '10px', border: '1px solid #333', backgroundColor: '#121212' },
    buttonRow: { display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '1.5rem' },
    cancelBtn: { backgroundColor: 'transparent', color: '#A0AEC0', border: '1px solid #333', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer' },

    actionRow: { display: 'flex', gap: '10px', marginTop: '15px', borderTop: '1px solid #333', paddingTop: '10px' },
    editBtn: { backgroundColor: 'transparent', color: '#00FF66', border: '1px solid #00FF66', padding: '5px 10px', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    deleteBtn: { backgroundColor: 'transparent', color: '#FF4444', border: '1px solid #FF4444', padding: '5px 10px', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' }
  };

  return (
    <div style={styles.pageContainer}>
      
      <div style={styles.headerRow}>
        <div>
          <h2 style={{ margin: '0 0 4px 0', fontSize: '24px' }}>Meal Catalog</h2>
          <p style={{ margin: 0, color: '#A0AEC0', fontSize: '14px' }}>Manage your menu offerings</p>
        </div>
        <button style={styles.addButton} onClick={() => setShowModal(true)}>
          + Add New Meal
        </button>
      </div>

      <div style={styles.catalogContainer}>
        {loading ? (
          <p style={{ color: '#A0AEC0' }}>Loading catalog...</p>
        ) : meals.length === 0 ? (
          <p style={{ color: '#A0AEC0' }}>No meals found. Click "Add New Meal" to get started!</p>
        ) : (
          <div style={styles.grid}>
            {meals.map((meal) => (
              <div key={meal.id} style={styles.card}>
                <img 
                  src={meal.imageUrl} 
                  alt={meal.name} 
                  style={styles.image}
                  onError={(e) => { e.target.src = 'https://via.placeholder.com/400x200?text=No+Image' }}
                />
                <div style={styles.actionRow}>
                    <button style={styles.editBtn} onClick={() => handleEditClick(meal)}>Edit</button>
                    <button style={styles.deleteBtn} onClick={() => handleDelete(meal.id)}>Delete</button>
                </div>

                <h3 style={{ margin: '0 0 4px 0', color: '#00FF66', fontSize: '18px' }}>{meal.name}</h3>
                <span style={styles.categoryTag}>{meal.category}</span>
                <p style={{ margin: '8px 0 0 0', color: '#A0AEC0', fontSize: '13px', lineHeight: '1.4' }}>
                  {meal.description}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>

      {showModal && (
        <div style={styles.modalOverlay}>
          <div style={styles.modalContent}>
            <h2 style={{ marginTop: 0, marginBottom: '1.5rem', color: '#FFF' }}>Add New Meal</h2>
            
            <form onSubmit={handleSubmit}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Meal Name</label>
                <input type="text" name="name" value={textData.name} onChange={handleInputChange} style={styles.input} required placeholder="e.g. Grilled Salmon with Asparagus" />
              </div>
              
              <div style={styles.formGroup}>
                <label style={styles.label}>Category</label>
                <select name="category" value={textData.category} onChange={handleInputChange} style={styles.input}>
                  <option value="HIGH_PROTEIN">High Protein</option>
                  <option value="VEGETARIAN">Vegetarian</option>
                  <option value="VEGAN">Vegan</option>
                  <option value="KETO">Keto</option>
                  <option value="PALEO">Paleo</option>
                </select>
              </div>

              {/* --- BROUGHT BACK: Visual File Upload Input --- */}
              <div style={styles.formGroup}>
                <label style={styles.label}>Meal Image</label>
                
                {imagePreview ? (
                    <div style={{ position: 'relative' }}>
                        <img src={imagePreview} alt="Selected preview" style={styles.imagePreview} />
                        <button type="button" onClick={() => { setSelectedFile(null); setImagePreview(null); }} style={{ position: 'absolute', top: 15, left: 115, backgroundColor: 'rgba(255,0,0,0.5)', border: 'none', color: 'white', borderRadius: '50%', width: '20px', height: '20px', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>X</button>
                    </div>
                ) : (
                    <div style={{ height: '100px', width: '100px', backgroundColor: '#121212', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center', border: '1px dashed #333' }}>
                        <p style={{ color: '#A0AEC0', fontSize: '12px', margin: 0 }}>Preview</p>
                    </div>
                )}
                
                <input 
                    type="file" 
                    accept="image/png, image/jpeg, image/gif" 
                    onChange={handleFileChange} 
                    style={{ ...styles.input, marginTop: '10px' }} 
                    required 
                />
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>Description</label>
                <textarea name="description" value={textData.description} onChange={handleInputChange} style={{ ...styles.input, height: '80px', resize: 'vertical' }} required placeholder="Describe the ingredients and benefits..." />
              </div>

              <div style={styles.buttonRow}>
                <button type="button" onClick={resetForm} style={styles.cancelBtn}>Cancel</button>
                <button type="submit" style={styles.addButton}>Save Meal</button>
              </div>
            </form>

          </div>
        </div>
      )}

    </div>
  );
};

export default MealCatalog;
