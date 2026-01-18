/**
 * Yalah L'Univ - Main JavaScript Application
 * UEMF Carpooling Application
 */

// Configuration
const CONFIG = {
    LOCATIONIQ_API_KEY: 'YOUR_LOCATIONIQ_API_KEY', // Replace with actual key
    JAWG_ACCESS_TOKEN: 'YOUR_JAWG_TOKEN', // Replace with actual token
    DEFAULT_CENTER: [34.0331, -5.0003], // Fès coordinates
    UEMF_CAMPUS: [34.0450, -5.0653] // UEMF Campus - Route Principale Fès-Meknès
};

/**
 * Initialize app when DOM is ready
 */
document.addEventListener('DOMContentLoaded', function () {
    console.log('[YalahApp] DOM loaded, initializing...');
    initializeDatePickers();
    initializeToasts();
    initializeMobileMenu();

    // Initialize location autocomplete with delay to ensure all elements are ready
    setTimeout(() => {
        console.log('[YalahApp] Initializing location autocomplete...');
        initializeLocationAutocomplete('input[name="departure"], input[name="arrival"], input[name="departureLocation"], input[name="arrivalLocation"]');
    }, 100);
});

/**
 * Initialize Flatpickr date/time pickers
 */
function initializeDatePickers() {
    // Date pickers
    document.querySelectorAll('.datepicker').forEach(el => {
        if (typeof flatpickr !== 'undefined') {
            flatpickr(el, {
                locale: 'fr',
                dateFormat: 'Y-m-d',
                minDate: 'today',
                disableMobile: true
            });
        }
    });

    // Time pickers
    document.querySelectorAll('.timepicker').forEach(el => {
        if (typeof flatpickr !== 'undefined') {
            flatpickr(el, {
                enableTime: true,
                noCalendar: true,
                dateFormat: 'H:i',
                time_24hr: true,
                minTime: '05:00',
                maxTime: '23:00'
            });
        }
    });
}

/**
 * Toast notifications system
 */
const Toast = {
    container: null,

    init() {
        this.container = document.createElement('div');
        this.container.className = 'toast-container';
        this.container.style.cssText = `
            position: fixed;
            top: 80px;
            right: 20px;
            z-index: 400;
            display: flex;
            flex-direction: column;
            gap: 10px;
        `;
        document.body.appendChild(this.container);
    },

    show(message, type = 'info', duration = 4000) {
        if (!this.container) this.init();

        const toast = document.createElement('div');
        toast.className = `alert alert-${type} animate-slide-up`;
        toast.style.cssText = `
            min-width: 300px;
            box-shadow: var(--shadow-lg);
        `;

        const icons = {
            success: '✓',
            error: '✕',
            warning: '⚠',
            info: 'ℹ'
        };

        toast.innerHTML = `
            <span style="font-size: 1.25rem;">${icons[type] || icons.info}</span>
            <span>${message}</span>
            <button onclick="this.parentElement.remove()" style="margin-left: auto; background: none; border: none; cursor: pointer; font-size: 1.25rem;">&times;</button>
        `;

        this.container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    },

    success(message) { this.show(message, 'success'); },
    error(message) { this.show(message, 'error'); },
    warning(message) { this.show(message, 'warning'); },
    info(message) { this.show(message, 'info'); }
};

function initializeToasts() {
    Toast.init();

    // Show toasts for URL params
    const params = new URLSearchParams(window.location.search);

    if (params.get('created') === 'true') {
        Toast.success('Trajet créé avec succès!');
    }
    if (params.get('confirmed') === 'true') {
        Toast.success('Réservation confirmée!');
    }
    if (params.get('cancelled') === 'true') {
        Toast.info('Réservation annulée.');
    }
    if (params.get('error')) {
        Toast.error(decodeURIComponent(params.get('error')));
    }
}

/**
 * Mobile menu toggle
 */
function initializeMobileMenu() {
    const toggle = document.querySelector('.navbar-toggle');
    const nav = document.querySelector('.navbar-nav');

    if (toggle && nav) {
        toggle.addEventListener('click', () => {
            nav.classList.toggle('show');
            nav.style.display = nav.classList.contains('show') ? 'flex' : '';
        });
    }
}

/**
 * Geocoding with OpenStreetMap Nominatim (free, no API key required)
 */
async function geocodeAddress(address) {
    try {
        const response = await fetch(
            `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(address)}&format=json&countrycodes=ma&limit=5`,
            {
                headers: {
                    'Accept-Language': 'fr'
                }
            }
        );
        const data = await response.json();

        if (data && data.length > 0) {
            return data.map(item => ({
                lat: parseFloat(item.lat),
                lng: parseFloat(item.lon),
                displayName: item.display_name,
                shortName: item.display_name.split(',').slice(0, 2).join(',')
            }));
        }
        return [];
    } catch (error) {
        console.error('Geocoding error:', error);
        return [];
    }
}

/**
 * Autocomplete for location inputs
 */
function initializeLocationAutocomplete(inputSelector) {
    const inputs = document.querySelectorAll(inputSelector);

    // Predefined locations - ALL VERIFIED GPS coordinates (January 2026)
    // Sources: Google Maps, Mapcarta, Wikipedia
    const uemfLocations = [
        // UEMF Campus: 34.045, -5.065 (Route Principale Fès-Meknès) - VERIFIED
        { name: 'Campus UEMF', fullName: 'Campus UEMF - Université Euro-Méditerranéenne de Fès, Route Fès-Meknès', lat: 34.0450, lng: -5.0653 },
        { name: 'UEMF - Entrée Principale', fullName: 'Entrée Principale UEMF, Route Principale Fès-Meknès', lat: 34.0450, lng: -5.0653 },
        // Bab Boujloud: 34.0617, -4.984 - VERIFIED
        { name: 'Bab Boujloud', fullName: 'Bab Boujloud - Porte Bleue, Médina de Fès', lat: 34.0617, lng: -4.9840 },
        // Fès Ville Nouvelle centre
        { name: 'Fès Ville Nouvelle', fullName: 'Fès Ville Nouvelle, Centre-ville', lat: 34.0331, lng: -5.0003 },
        // Fès Médina
        { name: 'Fès Médina', fullName: 'Fès Médina, Ancienne ville', lat: 34.0617, lng: -4.9769 },
        // Place Florence: 34.0421, -5.0 - VERIFIED
        { name: 'Place Florence', fullName: 'Place Florence, Fès Ville Nouvelle', lat: 34.0421, lng: -5.0000 },
        { name: 'Fès Centre', fullName: 'Fès Centre, Place Florence', lat: 34.0421, lng: -5.0000 },
        // Borj Fès Mall: 34.0461, -4.9947 - VERIFIED (Wikipedia)
        { name: 'Borj Fès', fullName: 'Borj Fès Mall, Route de Sefrou', lat: 34.0461, lng: -4.9947 },
        // Gare Fès: 34.0472, -5.005 - VERIFIED
        { name: 'Gare Fès', fullName: 'Gare Ferroviaire de Fès, Avenue des FAR', lat: 34.0472, lng: -5.0050 },
        // Aéroport Fès-Saïss: 33.927, -4.978 - VERIFIED
        { name: 'Aéroport Fès-Saïss', fullName: 'Aéroport International Fès-Saïss', lat: 33.9273, lng: -4.9780 },
        // Place Atlas
        { name: 'Place Atlas', fullName: 'Place Atlas, Fès Ville Nouvelle', lat: 34.0350, lng: -5.0030 },
        // Hay Saada / Quartier Saada
        { name: 'Quartier Saada', fullName: 'Quartier Saada, Fès', lat: 34.0250, lng: -5.0100 },
        { name: 'Hay Saada', fullName: 'Hay Saada, Fès', lat: 34.0250, lng: -5.0100 },
        // Ain Kadous
        { name: 'Ain Kadous', fullName: 'Ain Kadous, Fès', lat: 34.0500, lng: -4.9600 },
        // Meknès Place El Hedim: 33.8932, -5.5657 - VERIFIED
        { name: 'Meknès Centre', fullName: 'Meknès Centre-ville, Place El Hedim', lat: 33.8932, lng: -5.5657 },
        // Sefrou
        { name: 'Sefrou', fullName: 'Sefrou, Maroc', lat: 33.8303, lng: -4.8353 },
        // Ifrane
        { name: 'Ifrane', fullName: 'Ifrane, Maroc', lat: 33.5333, lng: -5.1167 },
        // Ouled Tayeb
        { name: 'Ouled Tayeb', fullName: 'Ouled Tayeb, Fès', lat: 33.9600, lng: -5.0000 }
    ];

    inputs.forEach(input => {
        // Create dropdown container
        const wrapper = document.createElement('div');
        wrapper.style.cssText = 'position: relative; width: 100%;';
        input.parentNode.insertBefore(wrapper, input);
        wrapper.appendChild(input);

        const dropdown = document.createElement('div');
        dropdown.className = 'location-dropdown';
        dropdown.style.cssText = `
            position: fixed;
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
            max-height: 250px;
            overflow-y: auto;
            z-index: 9999;
            display: none;
            min-width: 280px;
        `;
        document.body.appendChild(dropdown);

        let timeout = null;

        // Position dropdown correctly
        function positionDropdown() {
            const rect = input.getBoundingClientRect();
            dropdown.style.top = (rect.bottom + window.scrollY + 5) + 'px';
            dropdown.style.left = rect.left + 'px';
            dropdown.style.width = rect.width + 'px';
        }

        input.addEventListener('input', function () {
            clearTimeout(timeout);
            const query = this.value.trim().toLowerCase();

            if (query.length < 2) {
                dropdown.style.display = 'none';
                return;
            }

            // Filter local UEMF locations first
            const localResults = uemfLocations.filter(loc =>
                loc.name.toLowerCase().includes(query) ||
                loc.fullName.toLowerCase().includes(query)
            );

            timeout = setTimeout(async () => {
                // Get results from Nominatim API
                const apiResults = await geocodeAddress(query + ', Maroc');

                // Combine: local first, then API results
                const allResults = [
                    ...localResults.map(r => ({
                        shortName: r.name,
                        displayName: r.fullName,
                        lat: r.lat,
                        lng: r.lng,
                        isLocal: true
                    })),
                    ...apiResults.filter(r => !localResults.some(l =>
                        l.name.toLowerCase() === r.shortName.toLowerCase()
                    ))
                ];

                if (allResults.length > 0) {
                    dropdown.innerHTML = allResults.slice(0, 8).map(r => `
                        <div class="location-item" style="padding: 12px 15px; cursor: pointer; border-bottom: 1px solid #eee; transition: background 0.2s;"
                             data-lat="${r.lat}" data-lng="${r.lng}" data-name="${r.shortName}"
                             onmouseover="this.style.background='#f0f7ff'" onmouseout="this.style.background='white'">
                            <div style="font-weight: 600; color: #333;">${r.isLocal ? '📍 ' : ''}${r.shortName}</div>
                            <div style="font-size: 0.75rem; color: #666; margin-top: 2px;">${r.displayName}</div>
                        </div>
                    `).join('');

                    positionDropdown();
                    dropdown.style.display = 'block';

                    // Add click handlers
                    dropdown.querySelectorAll('.location-item').forEach(item => {
                        item.addEventListener('click', () => {
                            input.value = item.dataset.name;

                            // Handle both naming conventions:
                            // 'departure' -> 'departureLat' (search form)
                            // 'departureLocation' -> 'departureLat' (create form)
                            let baseName = input.name.replace('Location', '');

                            // Try to find the lat/lng inputs
                            let latInput = document.querySelector(`[name="${baseName}Lat"]`);
                            let lngInput = document.querySelector(`[name="${baseName}Lng"]`);

                            // Fallback: try with original name
                            if (!latInput) latInput = document.querySelector(`[name="${input.name}Lat"]`);
                            if (!lngInput) lngInput = document.querySelector(`[name="${input.name}Lng"]`);

                            // Also try by ID for create.jsp form
                            if (!latInput) latInput = document.getElementById(`${baseName}Lat`);
                            if (!lngInput) lngInput = document.getElementById(`${baseName}Lng`);

                            if (latInput) latInput.value = item.dataset.lat;
                            if (lngInput) lngInput.value = item.dataset.lng;

                            console.log('[Autocomplete] Saved coords:', baseName, item.dataset.lat, item.dataset.lng);
                            dropdown.style.display = 'none';
                        });
                    });
                } else {
                    dropdown.innerHTML = '<div style="padding: 12px 15px; color: #666;">Aucun résultat trouvé</div>';
                    positionDropdown();
                    dropdown.style.display = 'block';
                }
            }, 300);
        });

        // Show local suggestions on focus if empty
        input.addEventListener('focus', function () {
            if (this.value.length < 2) {
                // Build dropdown with geolocation option first (only for departure field)
                let html = '';

                // Add "My Location" option for departure fields
                if (input.name === 'departure' || input.name === 'departureLocation') {
                    html += `
                        <div class="location-item geolocation-btn" style="padding: 12px 15px; cursor: pointer; border-bottom: 2px solid #1a5fb4; background: linear-gradient(135deg, #f0f7ff 0%, #e3f0ff 100%);"
                             onmouseover="this.style.background='#d6e9ff'" onmouseout="this.style.background='linear-gradient(135deg, #f0f7ff 0%, #e3f0ff 100%)'">
                            <div style="font-weight: 600; color: #1a5fb4; display: flex; align-items: center; gap: 8px;">
                                <span style="font-size: 1.2rem;">📍</span> Utiliser ma position actuelle
                            </div>
                            <div style="font-size: 0.75rem; color: #666; margin-top: 2px;">Localisation GPS</div>
                        </div>
                    `;
                }

                // Add predefined locations
                html += uemfLocations.slice(0, 8).map(r => `
                    <div class="location-item" style="padding: 12px 15px; cursor: pointer; border-bottom: 1px solid #eee;"
                         data-lat="${r.lat}" data-lng="${r.lng}" data-name="${r.name}"
                         onmouseover="this.style.background='#f0f7ff'" onmouseout="this.style.background='white'">
                        <div style="font-weight: 600; color: #333;">📍 ${r.name}</div>
                        <div style="font-size: 0.75rem; color: #666; margin-top: 2px;">${r.fullName}</div>
                    </div>
                `).join('');

                dropdown.innerHTML = html;
                positionDropdown();
                dropdown.style.display = 'block';

                // Handle geolocation click
                const geoBtn = dropdown.querySelector('.geolocation-btn');
                if (geoBtn) {
                    geoBtn.addEventListener('click', async () => {
                        geoBtn.innerHTML = `
                            <div style="font-weight: 600; color: #1a5fb4; display: flex; align-items: center; gap: 8px;">
                                <span style="animation: spin 1s linear infinite;">⏳</span> Localisation en cours...
                            </div>
                        `;

                        if (navigator.geolocation) {
                            navigator.geolocation.getCurrentPosition(
                                async (position) => {
                                    const lat = position.coords.latitude;
                                    const lng = position.coords.longitude;

                                    // Reverse geocode to get address
                                    try {
                                        const response = await fetch(
                                            `https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lng}&format=json`,
                                            { headers: { 'Accept-Language': 'fr' } }
                                        );
                                        const data = await response.json();
                                        const address = data.address;
                                        const locationName = address.neighbourhood || address.suburb || address.city_district || address.city || 'Ma position';

                                        input.value = locationName;

                                        // Store coordinates in hidden fields
                                        let baseName = input.name.replace('Location', '');
                                        let latInput = document.querySelector(`[name="${baseName}Lat"]`);
                                        let lngInput = document.querySelector(`[name="${baseName}Lng"]`);
                                        if (!latInput) latInput = document.getElementById(`${baseName}Lat`);
                                        if (!lngInput) lngInput = document.getElementById(`${baseName}Lng`);
                                        if (latInput) latInput.value = lat;
                                        if (lngInput) lngInput.value = lng;

                                        dropdown.style.display = 'none';
                                        Toast.success(`Position: ${locationName}`);
                                    } catch (error) {
                                        input.value = 'Ma position actuelle';
                                        dropdown.style.display = 'none';
                                        Toast.error('Erreur de géolocalisation');
                                    }
                                },
                                (error) => {
                                    Toast.error('Impossible d\'obtenir votre position. Vérifiez les permissions.');
                                    dropdown.style.display = 'none';
                                },
                                { enableHighAccuracy: true, timeout: 10000 }
                            );
                        } else {
                            Toast.error('Géolocalisation non supportée par votre navigateur');
                            dropdown.style.display = 'none';
                        }
                    });
                }

                // Handle location item clicks
                dropdown.querySelectorAll('.location-item:not(.geolocation-btn)').forEach(item => {
                    item.addEventListener('click', () => {
                        input.value = item.dataset.name;
                        let baseName = input.name.replace('Location', '');
                        let latInput = document.querySelector(`[name="${baseName}Lat"]`);
                        let lngInput = document.querySelector(`[name="${baseName}Lng"]`);
                        if (!latInput) latInput = document.getElementById(`${baseName}Lat`);
                        if (!lngInput) lngInput = document.getElementById(`${baseName}Lng`);
                        if (latInput) latInput.value = item.dataset.lat;
                        if (lngInput) lngInput.value = item.dataset.lng;
                        dropdown.style.display = 'none';
                    });
                });
            }
        });

        // Hide dropdown when clicking outside
        document.addEventListener('click', (e) => {
            if (!wrapper.contains(e.target) && !dropdown.contains(e.target)) {
                dropdown.style.display = 'none';
            }
        });

        // Hide dropdown on escape
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                dropdown.style.display = 'none';
            }
        });

        // Reposition on scroll/resize
        window.addEventListener('scroll', () => {
            if (dropdown.style.display !== 'none') {
                positionDropdown();
            }
        });
    });
}

/**
 * Star rating component
 */
function initializeRating(container, initialValue = 0, onChange = null) {
    const stars = container.querySelectorAll('.rating-star');
    let currentValue = initialValue;

    stars.forEach((star, index) => {
        star.style.cursor = 'pointer';

        star.addEventListener('click', () => {
            currentValue = index + 1;
            updateStars();
            if (onChange) onChange(currentValue);
        });

        star.addEventListener('mouseenter', () => {
            highlightStars(index + 1);
        });
    });

    container.addEventListener('mouseleave', () => {
        updateStars();
    });

    function updateStars() {
        stars.forEach((s, i) => {
            s.classList.toggle('filled', i < currentValue);
        });
    }

    function highlightStars(count) {
        stars.forEach((s, i) => {
            s.classList.toggle('filled', i < count);
        });
    }

    updateStars();
}

/**
 * Format currency
 */
function formatCurrency(amount, currency = 'MAD') {
    return new Intl.NumberFormat('fr-MA', {
        style: 'currency',
        currency: currency
    }).format(amount);
}

/**
 * Format date
 */
function formatDate(dateString, options = {}) {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('fr-FR', {
        weekday: options.weekday || 'long',
        day: 'numeric',
        month: options.month || 'long',
        hour: options.time ? '2-digit' : undefined,
        minute: options.time ? '2-digit' : undefined
    }).format(date);
}

/**
 * Confirm dialog
 */
function confirmAction(message, onConfirm) {
    const modal = document.createElement('div');
    modal.className = 'modal-backdrop';
    modal.style.cssText = `
        position: fixed;
        inset: 0;
        background: rgba(0, 0, 0, 0.5);
        backdrop-filter: blur(4px);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 300;
    `;

    modal.innerHTML = `
        <div class="card animate-slide-up" style="max-width: 400px; margin: 20px;">
            <div class="card-body text-center">
                <svg width="48" height="48" fill="var(--warning)" viewBox="0 0 20 20" style="margin-bottom: 1rem;">
                    <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"/>
                </svg>
                <p style="margin-bottom: 1.5rem;">${message}</p>
                <div class="flex gap-md justify-center">
                    <button class="btn btn-ghost" onclick="this.closest('.modal-backdrop').remove()">Annuler</button>
                    <button class="btn btn-primary" id="confirmBtn">Confirmer</button>
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    modal.querySelector('#confirmBtn').addEventListener('click', () => {
        modal.remove();
        if (onConfirm) onConfirm();
    });

    modal.addEventListener('click', (e) => {
        if (e.target === modal) modal.remove();
    });
}

/**
 * Form validation helpers
 */
function validateEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validateUEMFEmail(email) {
    return email.toLowerCase().endsWith('@ueuromed.org');
}

function validatePhone(phone) {
    return /^(\+212|0)[5-7]\d{8}$/.test(phone.replace(/\s/g, ''));
}

/**
 * Debounce function
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Export for use in other scripts
window.YalahApp = {
    Toast,
    geocodeAddress,
    formatCurrency,
    formatDate,
    confirmAction,
    validateEmail,
    validateUEMFEmail,
    debounce,
    CONFIG
};
