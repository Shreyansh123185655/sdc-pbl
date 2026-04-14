FROM nginx:alpine

# Copy frontend files
COPY index.html /usr/share/nginx/html/

# Create output directory
RUN mkdir -p /usr/share/nginx/html/output

# Expose port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
