#user input
length = float(input("Length (ft): "))
width = float(input("Width (ft): "))
thickness = float(input("Thickness (in): "))

employees = int(input("# of employees: "))


#size calculations
area = length * width
vol = area * (thickness/12)

#volume in cubic yards + 10% extra
volCY = (vol + (0.10 * vol))/27


#time calculations
emplTimeEst = (volCY * 3)/employees
pourTime = (volCY * 5)/60

totTime = emplTimeEst + pourTime + 1


#cost calculations
matCost = volCY * 125
laborCost = totTime * employees * 21

total = matCost + laborCost