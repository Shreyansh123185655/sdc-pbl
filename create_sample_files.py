import pandas as pd
import random

# Sample data
names = ['Aarav Sharma', 'Priya Patel', 'Rohit Verma', 'Sneha Gupta', 'Arjun Singh', 
         'Kavya Reddy', 'Vikram Malhotra', 'Ananya Joshi', 'Rohan Mehta', 'Isha Singh']
enrollments = ['EN2024001', 'EN2024002', 'EN2024003', 'EN2024004', 'EN2024005',
               'EN2024006', 'EN2024007', 'EN2024008', 'EN2024009', 'EN2024010']
classes = ['CS-A', 'CS-B', 'CS-A', 'CS-B', 'CS-A', 'CS-B', 'CS-A', 'CS-B', 'CS-A', 'CS-B']

# Questions data
questions = [
    'What is 2+2?', 'What is the capital of France?', 'Which language is used for web development?',
    'What is 5*3?', 'Who invented the telephone?', 'What is the largest planet?',
    'What is 10-7?', 'What is H2O?', 'Which programming language is object-oriented?',
    'What is 3+4?', 'What is the currency of Japan?', 'What does CPU stand for?',
    'What is 8/2?', 'Who wrote Romeo and Juliet?', 'What is the smallest prime number?',
    'What is 6*7?', 'What is the capital of Japan?', 'Which database is NoSQL?'
]

options = [
    ['3', '4', '5', '6'], ['London', 'Paris', 'Berlin', 'Madrid'], 
    ['JavaScript', 'Python', 'Java', 'C++'],
    ['15', '16', '17', '18'], ['Bell', 'Edison', 'Tesla', 'Marconi'],
    ['Earth', 'Mars', 'Jupiter', 'Saturn'],
    ['2', '3', '4', '5'], ['Water', 'Oxygen', 'Hydrogen', 'Carbon'],
    ['Python', 'C', 'Java', 'Assembly'],
    ['6', '7', '8', '9'], ['Yuan', 'Won', 'Yen', 'Rupee'],
    ['Central Processing Unit', 'Computer Processing Unit', 'Central Program Unit', 'Computer Program Unit'],
    ['3', '4', '5', '6'], ['Shakespeare', 'Dickens', 'Austen', 'Orwell'],
    ['1', '2', '3', '5'], ['42', '36', '48', '56'], ['Tokyo', 'Kyoto', 'Osaka', 'Nagoya'],
    ['MongoDB', 'MySQL', 'PostgreSQL', 'SQLite']
]

correct_answers = ['A', 'B', 'A', 'A', 'C', 'B', 'A', 'A', 'B', 'C', 'A', 'B', 'A', 'A', 'B', 'A']

# File 1: Basic Student Format (10 students, 5 questions)
print('📄 Creating file1_basic_students.xlsx...')
file1_data = []
for i in range(10):
    row = {'Name': names[i], 'EnrollmentNo': enrollments[i], 'Class': classes[i]}
    for q in range(5):
        row[f'Question{q+1}'] = questions[q]
        row[f'OptionA_{q+1}'] = options[q][0]
        row[f'OptionB_{q+1}'] = options[q][1]
        row[f'OptionC_{q+1}'] = options[q][2]
        row[f'OptionD_{q+1}'] = options[q][3]
        row[f'CorrectAnswer{q+1}'] = correct_answers[q]
    file1_data.append(row)

df1 = pd.DataFrame(file1_data)
df1.to_excel('file1_basic_students.xlsx', index=False)

# File 2: Advanced Student Format (15 students, 10 questions)
print('📄 Creating file2_advanced_students.xlsx...')
file2_data = []
for i in range(15):
    idx = i % len(names)
    row = {'Name': names[idx], 'EnrollmentNo': enrollments[idx], 'Class': classes[idx], 'Section': 'A'}
    for q in range(10):
        row[f'Q{q+1}'] = questions[q]
        row[f'A{q+1}'] = options[q][0]
        row[f'B{q+1}'] = options[q][1]
        row[f'C{q+1}'] = options[q][2]
        row[f'D{q+1}'] = options[q][3]
        row[f'Ans{q+1}'] = correct_answers[q]
    file2_data.append(row)

df2 = pd.DataFrame(file2_data)
df2.to_excel('file2_advanced_students.xlsx', index=False)

# File 3: Simple Student Format (5 students, 3 questions)
print('📄 Creating file3_simple_students.xlsx...')
file3_data = []
for i in range(5):
    row = {'StudentName': names[i], 'RollNo': enrollments[i], 'Grade': '10th'}
    for q in range(3):
        row[f'Question_{q+1}'] = questions[q]
        row[f'Opt1_{q+1}'] = options[q][0]
        row[f'Opt2_{q+1}'] = options[q][1]
        row[f'Opt3_{q+1}'] = options[q][2]
        row[f'Opt4_{q+1}'] = options[q][3]
        row[f'Answer_{q+1}'] = correct_answers[q]
    file3_data.append(row)

df3 = pd.DataFrame(file3_data)
df3.to_excel('file3_simple_students.xlsx', index=False)

# File 4: Complete Student Format (8 students, 15 questions)
print('📄 Creating file4_complete_students.xlsx...')
file4_data = []
for i in range(8):
    idx = i % len(names)
    row = {'Name': names[idx], 'EnrollmentNo': enrollments[idx], 'Class': classes[idx], 'Section': 'A', 'Batch': '2024'}
    for q in range(15):
        row[f'Q{q+1}'] = questions[q]
        row[f'A{q+1}'] = options[q][0]
        row[f'B{q+1}'] = options[q][1]
        row[f'C{q+1}'] = options[q][2]
        row[f'D{q+1}'] = options[q][3]
        row[f'Correct{q+1}'] = correct_answers[q]
    file4_data.append(row)

df4 = pd.DataFrame(file4_data)
df4.to_excel('file4_complete_students.xlsx', index=False)

print('✅ All 4 Excel files created successfully!')
print('\n📁 Files created:')
print('1. file1_basic_students.xlsx (10 students, 5 questions)')
print('2. file2_advanced_students.xlsx (15 students, 10 questions)')
print('3. file3_simple_students.xlsx (5 students, 3 questions)')
print('4. file4_complete_students.xlsx (8 students, 15 questions)')
