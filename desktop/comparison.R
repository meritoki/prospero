
library("TSclust")

args <- commandArgs(trailingOnly = TRUE)

##### EXAMPLE 2.1 INTEREST RATES DATASET - PREDICTION BASED DISTANCE #####

# load the dataset
data <- read.csv(args[1])
data[, colSums(data != 0) > 0]
interest.rates <- ts(data,start=c(as.numeric(args[2]),as.numeric(args[3])), end=c(as.numeric(args[4]),as.numeric(args[5])), frequency=12)
set.seed(35000)

##### EXAMPLE 2.2 INTEREST RATES DATASET - DISSIMILARITY COMPARISON #####
relative.rate.change <- diff(log(interest.rates+0.00001), 1)
relative.rate.change[is.na(relative.rate.change)] <- 0
print(relative.rate.change)
# 5-cluster solutions will be stored in 'Five.cluster.sol'' matrix
Five.cluster.sol <- matrix(0, nrow = ncol(relative.rate.change), ncol = 3)
colnames(Five.cluster.sol) <- c("ACF", "LNP", "PIC")
rownames(Five.cluster.sol) <- colnames(relative.rate.change)
# obtaining the 5-cluster solutions with diss.ACF, diss.PER and diss.AR.PIC
Five.cluster.sol[, 1] <- cutree(hclust(diss(relative.rate.change, "ACF", p = 0.05)),
  k = 8)
Five.cluster.sol[, 2] <- cutree(hclust(diss(relative.rate.change, "PER", normalize = TRUE,
  logarithm = TRUE)), k = 8)
Five.cluster.sol[, 3] <- cutree(hclust(diss(relative.rate.change, "AR.PIC")), k = 8)
# show the solution
Five.cluster.sol
# write.csv(Five.cluster.sol, "./output.csv")
write.csv(Five.cluster.sol, args[6])
