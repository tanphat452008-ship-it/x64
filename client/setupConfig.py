import json
import hashlib
import os

path = "/storage/emulated/0/Android/"
url = "http://104.234.180.156/Mobile/Game/"
//a
# Define the list of special files
special_files = ["special_file_1.txt", "special_file_2.txt"]  # Add your special file names here

def create_json_file(file_info_list, output_filename="files.json"):
    """Creates a JSON file from a list of file information dictionaries."""
    with open(output_filename, 'w') as f:
        json.dump(file_info_list, f, indent=2)

def calculate_md5_hash(filename):
    """Calculates the MD5 hash of a file."""
    hasher = hashlib.md5()
    with open(filename, 'rb') as file:
        while True:
            chunk = file.read(4096)
            if not chunk:
                break
            hasher.update(chunk)
    return hasher.hexdigest()

def get_file_info(filepath):
    """Gets file information (name, path, size, hash) for a given filepath."""
    file_name = os.path.basename(filepath)
    file_info = {}
    file_info["hash"] = calculate_md5_hash(filepath)
    file_info["name"] = file_name
    file_info["path"] = path + filepath.replace("\\", "/")
    file_info["size"] = str(os.path.getsize(filepath))
    file_info["url"] = url + file_name
    
    # Check if the file is in the special list
    if file_name in special_files:
        file_info["ignore"] = 1
    else:
        file_info["ignore"] = 0
    
    return file_info

def get_all_files(folder_path):
    """Gets a list of all files in a folder and its subfolders."""
    all_files = []
    for root, _, files in os.walk(folder_path):
        for file in files:
            filepath = os.path.join(root, file)
            all_files.append(filepath)
    return all_files

# Example usage:
if __name__ == "__main__":
    folder_path = "Game"

    try:
        all_files = get_all_files(folder_path)
        json_data = []
        for filepath in all_files:
            file_info = get_file_info(filepath)
            json_data.append(file_info)

        create_json_file(json_data)
        print("JSON file created successfully!")

    except FileNotFoundError:
        print(f"Error: Folder not found at '{folder_path}'")
